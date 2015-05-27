package clive.peer.activehelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import clive.main.Configuration;
import clive.peer.common.BlockData;
import clive.peer.common.Buffer;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;
import clive.peer.common.MSPeerDescriptor;
import clive.peer.common.RandomView;
import clive.peer.membership.Gradient;
import clive.peer.membership.GradientConfiguration;
import clive.peer.membership.GradientInit;
import clive.peer.membership.GradientPeerSampling;
import clive.peer.membership.Join;
import clive.peer.membership.JoinCompleted;
import clive.peer.mspeers.MSConfiguration;
import clive.peer.mspeers.MessagePort;
import clive.peer.partnership.RegisterResponse;
import clive.simulator.snapshot.Snapshot;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.p2p.bootstrap.BootstrapCompleted;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.bootstrap.BootstrapRequest;
import se.sics.kompics.p2p.bootstrap.BootstrapResponse;
import se.sics.kompics.p2p.bootstrap.P2pBootstrap;
import se.sics.kompics.p2p.bootstrap.PeerEntry;
import se.sics.kompics.p2p.bootstrap.client.BootstrapClient;
import se.sics.kompics.p2p.bootstrap.client.BootstrapClientInit;
import se.sics.kompics.p2p.fd.FailureDetector;
import se.sics.kompics.p2p.fd.PeerFailureSuspicion;
import se.sics.kompics.p2p.fd.StartProbingPeer;
import se.sics.kompics.p2p.fd.StopProbingPeer;
import se.sics.kompics.p2p.fd.SuspicionStatus;
import se.sics.kompics.p2p.fd.ping.PingFailureDetector;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorInit;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timer;

public final class ActiveHelper extends ComponentDefinition {
	
	Negative<ActiveHelperPort> helperPort = negative(ActiveHelperPort.class);

	Positive<MessagePort> messagePort = positive(MessagePort.class);
	Positive<Network> network = positive(Network.class);
	Positive<Timer> timer = positive(Timer.class);

	private Component membership;
	private Component fd, bootstrap;
	
	private Address self;
	private MSPeerAddress msPeerSelf;
	private MSPeerAddress mediaSource;

	private Buffer buffer;
	
	private RandomView randomView;
	
	private int blockSize;
	
	private int bootstrapRequestPeerCount;
	private boolean bootstrapped;

	private BootstrapConfiguration bootstrapConfiguration;
	private MSPeerAddress[] helperChildren = new MSPeerAddress[Configuration.MAX_HELPER_CHILDREN];
	private int numHelperChild = 0;
	private MSPeerAddress helperParent;
	private int helperTurn = 0;

	private ArrayList<MSPeerAddress> nodes = new ArrayList<MSPeerAddress>();

	private HashMap<Address, UUID> fdRequests = new HashMap<Address, UUID>();
	private HashMap<Address, MSPeerAddress> fdNodes = new HashMap<Address, MSPeerAddress>();

//-------------------------------------------------------------------	
	public ActiveHelper() {
		membership = create(Gradient.class);
		fd = create(PingFailureDetector.class);
		bootstrap = create(BootstrapClient.class);

		//connect(network, membership.getNegative(Network.class));
		//connect(network, partnership.getNegative(Network.class));
		//connect(network, streaming.getNegative(Network.class));
		connect(network, fd.getNegative(Network.class));
		connect(network, bootstrap.getNegative(Network.class));
		connect(timer, membership.getNegative(Timer.class));
		connect(timer, fd.getNegative(Timer.class));
		connect(timer, bootstrap.getNegative(Timer.class));
		
		subscribe(handleInit, control);
		subscribe(handleJoin, helperPort);
		subscribe(handleUpdateNodes, timer);
		subscribe(handleJoinCompleted, membership.getPositive(GradientPeerSampling.class));
		subscribe(handleBootstrapResponse, bootstrap.getPositive(P2pBootstrap.class));
		subscribe(handleRecvMSMessage, messagePort);
		subscribe(handleSendMSMessage, membership.getNegative(MessagePort.class));
		subscribe(handleTreeJoinRequest, messagePort);
		subscribe(handleTreeJoinResponse, messagePort);
		subscribe(handleBlockData, messagePort);
		subscribe(handleRecvResponseHelperNodes, messagePort);
		subscribe(handlePeerFailureSuspicion, fd.getPositive(FailureDetector.class));
	}

//-------------------------------------------------------------------	
	Handler<ActiveHelperInit> handleInit = new Handler<ActiveHelperInit>() {
		public void handle(ActiveHelperInit init) {
			msPeerSelf = init.getMSPeerSelf();
			self = msPeerSelf.getPeerAddress();
			
			randomView = new RandomView(init.getGradientConfiguration().getRandomViewSize(), msPeerSelf);
			
			MSConfiguration msConfiguration = init.getMSConfiguration();
			GradientConfiguration gradientCOnfiguration = init.getGradientConfiguration();
			bootstrapConfiguration = init.getBootstrapConfiguration();
			
			blockSize = msConfiguration.getBlockSize();
			buffer = new Buffer(msConfiguration.getBufferSize(), Configuration.WINDOW_SIZE);

			bootstrapRequestPeerCount = gradientCOnfiguration.getBootstrapRequestPeerCount();
			mediaSource = init.getSourceAddress();

			trigger(new GradientInit(gradientCOnfiguration, randomView, Configuration.ACTIVE_HELPER_FANOUT), membership.getControl());
			trigger(new BootstrapClientInit(self, bootstrapConfiguration), bootstrap.getControl());
			trigger(new PingFailureDetectorInit(self, init.getFdConfiguration()), fd.getControl());
		}
	};

//-------------------------------------------------------------------	
	Handler<JoinActiveHelper> handleJoin = new Handler<JoinActiveHelper>() {
		public void handle(JoinActiveHelper event) {
			BootstrapRequest request = new BootstrapRequest("GradienTV", bootstrapRequestPeerCount);
			trigger(request, bootstrap.getPositive(P2pBootstrap.class));

			trigger(new TreeJoinRequest(msPeerSelf, mediaSource), messagePort);
			
			SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(0, 5000);
			spt.setTimeoutEvent(new UpdateNodesTimeout(spt));
			trigger(spt, timer);
			
			Snapshot.addHelper(msPeerSelf);			
		}
	};

//-------------------------------------------------------------------	
	Handler<BootstrapResponse> handleBootstrapResponse = new Handler<BootstrapResponse>() {
		public void handle(BootstrapResponse event) {
			if (!bootstrapped) {
				Set<PeerEntry> somePeers = event.getPeers();
				LinkedList<MSPeerAddress> insiders = new LinkedList<MSPeerAddress>();
				
				for (PeerEntry peerEntry : somePeers)
					insiders.add((MSPeerAddress) peerEntry.getOverlayAddress());
				
				trigger(new Join(msPeerSelf, insiders), membership.getPositive(GradientPeerSampling.class));
				bootstrapped = true;
			}
		}
	};

//-------------------------------------------------------------------	
	Handler<JoinCompleted> handleJoinCompleted = new Handler<JoinCompleted>() {
		public void handle(JoinCompleted event) {
			// bootstrap completed
			trigger(new BootstrapCompleted("GradienTV", msPeerSelf), bootstrap.getPositive(P2pBootstrap.class));
		}
	};
	
//-------------------------------------------------------------------	
	Handler<BlockData> handleBlockData = new Handler<BlockData>() {
		public void handle(BlockData event) {
		
			long blockIndex = event.getBlockIndex();
			buffer.addBlock(blockIndex);
						
			for (MSPeerAddress node : nodes)
				trigger(new BlockData(msPeerSelf, node, blockIndex, blockSize), messagePort);
			
			for (int i = 0; i < Configuration.MAX_HELPER_CHILDREN; i++) {
				if (helperChildren[i] != null)
					trigger(new BlockData(msPeerSelf, helperChildren[i], blockIndex, blockSize), messagePort);
			}
		}		
	};

//-------------------------------------------------------------------	
	Handler<MSMessage> handleSendMSMessage = new Handler<MSMessage>() {
		public void handle(MSMessage event) {			
			trigger(event, messagePort);
		}
	};

//-------------------------------------------------------------------	
	Handler<MSMessage> handleRecvMSMessage = new Handler<MSMessage>() {
		public void handle(MSMessage event) {
			trigger(event, membership.getNegative(MessagePort.class));
		}
	};

//-------------------------------------------------------------------	
	Handler<PeerFailureSuspicion> handlePeerFailureSuspicion = new Handler<PeerFailureSuspicion>() {
		public void handle(PeerFailureSuspicion event) {
			Address suspectedPeerAddress = event.getPeerAddress();
			MSPeerAddress suspectedPeer;
			
			if (event.getSuspicionStatus().equals(SuspicionStatus.SUSPECTED)) {
				if (!fdNodes.containsKey(suspectedPeerAddress) || !fdRequests.containsKey(suspectedPeerAddress))
					return;
				
				suspectedPeer = fdNodes.get(suspectedPeerAddress);
				nodes.remove(suspectedPeer);
				fdUnregister(suspectedPeer);
			}
		}
	};
	
//-------------------------------------------------------------------	
	Handler<TreeJoinRequest> handleTreeJoinRequest = new Handler<TreeJoinRequest>() {
		public void handle(TreeJoinRequest event) {
			MSPeerAddress peer = event.getMSPeerSource();
			
			if (numHelperChild < Configuration.MAX_HELPER_CHILDREN) {
				if (nodes.size() + helperChildren.length >= Configuration.ACTIVE_HELPER_FANOUT) {
					MSPeerAddress cheapestChild = nodes.get(nodes.size() - 1);		
					nodes.remove(cheapestChild);					
				}				
				
				helperChildren[numHelperChild] = peer;
				numHelperChild++;
				
				trigger(new TreeJoinResponse(msPeerSelf, peer, null, RegisterResponse.ACCEPTED), messagePort);
				
				ArrayList<MSPeerAddress> helperChildrenList = new ArrayList<MSPeerAddress>();
				for (MSPeerAddress child : helperChildren)
					helperChildrenList.add(child);
				Snapshot.helperUpdateChildren(msPeerSelf, helperChildrenList);
				//System.out.println(self + " -> child: " + peer);
			} else {
				helperTurn = (helperTurn + 1) % Configuration.MAX_HELPER_CHILDREN;				
				trigger(new TreeJoinResponse(msPeerSelf, peer, helperChildren[helperTurn], RegisterResponse.DENIED), messagePort);
			}
		}
	};

//-------------------------------------------------------------------	
	Handler<TreeJoinResponse> handleTreeJoinResponse = new Handler<TreeJoinResponse>() {
		public void handle(TreeJoinResponse event) {
			MSPeerAddress peer = event.getMSPeerSource();
			RegisterResponse result = event.getResult();
			
			if (result == RegisterResponse.ACCEPTED) {
				helperParent = peer;
				ArrayList<MSPeerAddress> helperParents = new ArrayList<MSPeerAddress>();
				helperParents.add(helperParent);
				Snapshot.helperUpdateParents(msPeerSelf, helperParents);
				//System.out.println(self + " -> parent: " + helperParent);
			} else
				trigger(new TreeJoinRequest(msPeerSelf, event.getHelper()), messagePort);			
		}
	};

//-------------------------------------------------------------------	
	Handler<UpdateNodesTimeout> handleUpdateNodes = new Handler<UpdateNodesTimeout>() {
		public void handle(UpdateNodesTimeout event) {
			trigger(new RequestHelperNodes(msPeerSelf, mediaSource), messagePort);
		}
	};

//-------------------------------------------------------------------	
	Handler<ResponseHelperNodes> handleRecvResponseHelperNodes = new Handler<ResponseHelperNodes>() {
		public void handle(ResponseHelperNodes event) {
			ArrayList<MSPeerAddress> allHelpersNodes = event.getNodes();
			
			ArrayList<MSPeerAddress> duplicateNodes = new ArrayList<MSPeerAddress>();
			for (MSPeerAddress node : nodes) {
				if (allHelpersNodes.contains(node))
					duplicateNodes.add(node);
			}
			
			for (MSPeerAddress node : duplicateNodes)
				nodes.remove(node);			
			
			if (nodes.size() < Configuration.ACTIVE_HELPER_FANOUT - numHelperChild) {
				for (MSPeerDescriptor desc : randomView.getAll()) {
					MSPeerAddress node = desc.getMSPeerAddress();
					if (!nodes.contains(node) && !allHelpersNodes.contains(node)) {
						nodes.add(node);
						fdRegister(node);
					}
					
					if (nodes.size() >= Configuration.ACTIVE_HELPER_FANOUT - numHelperChild)
						break;
				}
			}
			
			//System.out.println(msPeerSelf + " -> " + nodes);
			
			trigger(new RegisterHelperNodes(msPeerSelf, mediaSource, nodes), messagePort);
		}
	};
	
//-------------------------------------------------------------------	
	private void fdRegister(MSPeerAddress peer) {
		Address peerAddress = peer.getPeerAddress();
		StartProbingPeer spp = new StartProbingPeer(peerAddress, peer);
		trigger(spp, fd.getPositive(FailureDetector.class));
		
		fdRequests.put(peerAddress, spp.getRequestId());
		fdNodes.put(peerAddress, peer);
	}

//-------------------------------------------------------------------	
	private void fdUnregister(MSPeerAddress peer) {
		if (peer == null)
			return;
			
		Address peerAddress = peer.getPeerAddress();

		trigger(new StopProbingPeer(peerAddress, fdRequests.get(peerAddress)), fd.getPositive(FailureDetector.class));

		fdRequests.remove(peerAddress);
		fdNodes.remove(peerAddress);
	}
}
