package clive.peer.source;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import clive.main.Configuration;
import clive.peer.activehelper.RegisterHelperNodes;
import clive.peer.activehelper.RequestHelperNodes;
import clive.peer.activehelper.ResponseHelperNodes;
import clive.peer.activehelper.TreeJoinRequest;
import clive.peer.activehelper.TreeJoinResponse;
import clive.peer.common.BlockData;
import clive.peer.common.Buffer;
import clive.peer.common.HelperType;
import clive.peer.common.MSComponents;
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
import clive.peer.partnership.Partnership;
import clive.peer.partnership.PartnershipPort;
import clive.peer.partnership.RegisterResponse;
import clive.peer.partnership.StartPartnership;
import clive.simulator.core.DontNeedHelper;
import clive.simulator.core.NeedHelper;
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

public final class Source extends ComponentDefinition {
	
	Negative<SourcePort> sourcePort = negative(SourcePort.class);

	Positive<MessagePort> messagePort = positive(MessagePort.class);
	Positive<Network> network = positive(Network.class);
	Positive<Timer> timer = positive(Timer.class);

	private Component membership, partnership;
	private Component fd, bootstrap;
	
	private Address self;
	private MSPeerAddress msPeerSelf;

	private int uploadSlots;
	private Buffer buffer;
	
	private RandomView randomView;
	
	private int streamRate;
	private int blockSize;
	private long newBlock;
	
	private int bootstrapRequestPeerCount;
	private boolean bootstrapped;

	private BootstrapConfiguration bootstrapConfiguration;
	private MSPeerAddress passiveHelper = null;
	private LinkedList<MSPeerAddress> activeHelpers = new LinkedList<MSPeerAddress>();
	private MSPeerAddress[] helperChildren = new MSPeerAddress[Configuration.MAX_HELPER_CHILDREN];
	private int numHelperChild = 0;
	private int helperTurn = 0;

	private ArrayList<MSPeerAddress> nodes = new ArrayList<MSPeerAddress>();

	private HashMap<Address, UUID> fdRequests = new HashMap<Address, UUID>();
	private HashMap<Address, MSPeerAddress> fdNodes = new HashMap<Address, MSPeerAddress>();
	
	private HashMap<MSPeerAddress, ArrayList<MSPeerAddress>> helperNodes = new HashMap<MSPeerAddress, ArrayList<MSPeerAddress>>();
	
	private ArrayList<double[]> distributionHistorty = new ArrayList<double[]>();
	private ArrayList<Double> netSizeHistorty = new ArrayList<Double>();
	private Random rnd = new Random(0);
	
	private static int TD = 1;
	private static int TQOS = 3;
	
	private double netSize;
	private int infectedNodes;
	private double[] bwDistribution = new double[Configuration.SLOT_RANGE];

//-------------------------------------------------------------------	
	public Source() {
		membership = create(Gradient.class);
		partnership = create(Partnership.class);
		fd = create(PingFailureDetector.class);
		bootstrap = create(BootstrapClient.class);

		//connect(network, membership.getNegative(Network.class));
		//connect(network, partnership.getNegative(Network.class));
		//connect(network, streaming.getNegative(Network.class));
		connect(network, fd.getNegative(Network.class));
		connect(network, bootstrap.getNegative(Network.class));
		connect(timer, membership.getNegative(Timer.class));
		connect(timer, partnership.getNegative(Timer.class));
		connect(timer, fd.getNegative(Timer.class));
		connect(timer, bootstrap.getNegative(Timer.class));
		
		connect(partnership.getNegative(FailureDetector.class), fd.getPositive(FailureDetector.class));
		
		subscribe(handleInit, control);
		subscribe(handleJoin, sourcePort);
		subscribe(handleAddHelper, sourcePort);
		subscribe(handleJoinCompleted, membership.getPositive(GradientPeerSampling.class));
		subscribe(handleBootstrapResponse, bootstrap.getPositive(P2pBootstrap.class));
		subscribe(handleNeedHelper, timer);
		subscribe(handleGenerateStream, timer);
		subscribe(handleDistributionTimeout, timer);
		subscribe(handleRecvMSMessage, messagePort);
		subscribe(handleSendMSMessage, partnership.getNegative(MessagePort.class));
		subscribe(handleSendMSMessage, membership.getNegative(MessagePort.class));
		subscribe(handleTreeJoinRequest, messagePort);
		subscribe(handleRegisterHelperNodes, messagePort);
		subscribe(handleRequestHelperNodes, messagePort);
		subscribe(handleDistributionResponse, messagePort);
		subscribe(handlePeerFailureSuspicion, fd.getPositive(FailureDetector.class));
	}

//-------------------------------------------------------------------	
	Handler<SourceInit> handleInit = new Handler<SourceInit>() {
		public void handle(SourceInit init) {
			msPeerSelf = init.getMSPeerSelf();
			self = msPeerSelf.getPeerAddress();
			uploadSlots = init.getUploadSlots();			
			
			randomView = new RandomView(init.getGradientConfiguration().getRandomViewSize(), msPeerSelf);
			
			newBlock = 0;
			MSConfiguration msConfiguration = init.getMSConfiguration();
			GradientConfiguration gradientCOnfiguration = init.getGradientConfiguration();
			bootstrapConfiguration = init.getBootstrapConfiguration();
			
			streamRate = msConfiguration.getStreamRate();
			blockSize = msConfiguration.getBlockSize();			
			buffer = new Buffer(msConfiguration.getBufferSize(), Configuration.WINDOW_SIZE);

			bootstrapRequestPeerCount = gradientCOnfiguration.getBootstrapRequestPeerCount();

			trigger(new GradientInit(gradientCOnfiguration, randomView, uploadSlots), membership.getControl());
			//trigger(new PartnershipInit(msPeerSelf, randomView, uploadSlots, buffer, msConfiguration, init.getBW(), passiveHelper, msPeerSelf, helperChildren), partnership.getControl());
			trigger(new BootstrapClientInit(self, bootstrapConfiguration), bootstrap.getControl());
			trigger(new PingFailureDetectorInit(self, init.getFdConfiguration()), fd.getControl());
		}
	};

//-------------------------------------------------------------------	
	Handler<JoinSource> handleJoin = new Handler<JoinSource>() {
		public void handle(JoinSource event) {
			BootstrapRequest request = new BootstrapRequest("GradienTV", bootstrapRequestPeerCount);
			trigger(request, bootstrap.getPositive(P2pBootstrap.class));

			Snapshot.addPeer(msPeerSelf, uploadSlots, buffer, System.currentTimeMillis());
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
			
			// start updating the neighbours
			trigger(new StartPartnership(), partnership.getPositive(PartnershipPort.class));
			
			// schedule for stream generation
			long blockGenerationPeriod = (long)(((blockSize * 1000) / streamRate) * Configuration.STRIPES);
			System.out.println("-------------> block size: " + blockSize + ", rate: " + streamRate + ", period: " + blockGenerationPeriod);
			
			SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(blockGenerationPeriod, blockGenerationPeriod);
			spt.setTimeoutEvent(new GenerateStream(spt));
			trigger(spt, timer);

			SchedulePeriodicTimeout dr = new SchedulePeriodicTimeout(10000, 10000);
			dr.setTimeoutEvent(new DistributionTimeout(dr));
			trigger(dr, timer);

			SchedulePeriodicTimeout nh = new SchedulePeriodicTimeout(20000, 20000);
			nh.setTimeoutEvent(new NeedHelperTimeout(nh));
			trigger(nh, timer);

		}
	};

//-------------------------------------------------------------------	
	Handler<NeedHelperTimeout> handleNeedHelper = new Handler<NeedHelperTimeout>() {
		public void handle(NeedHelperTimeout event) {
			if (netSize == 0)
				return;
			
			int delta = Configuration.ACTIVE_HELPER_FANOUT;

			int n;
			int srcMinSize = 0;
			for (int i = 0; i < 100; i++) {
				n = calcInfectedNodes(bwDistribution, TQOS);
				if (srcMinSize < n)
					srcMinSize = n;
			}

			int ahMinSize = 0;
			for (int i = 0; i < 100; i++) {
				n = calcInfectedNodes(bwDistribution, TQOS - TD);
				if (ahMinSize < n)
					ahMinSize = n;
			}
			
			infectedNodes = srcMinSize * (Configuration.SRC_FANOUT - helperChildren.length) + helperNodes.size() * (Configuration.ACTIVE_HELPER_FANOUT - helperChildren.length) * ahMinSize;
//						
			System.out.println("+++++++++++++++++++");
			DecimalFormat df = new DecimalFormat("#.##");
			String str = "source distribution: "; 
			for (int i = 0; i < 11; i++)
				str += df.format(bwDistribution[i]) + ", ";
			System.out.println(str);
			System.out.println("net size: " + netSize + ", infected nodes: " + infectedNodes);
			System.out.println("srcMinSize: " + srcMinSize + ", ahMinSize: " + ahMinSize + ", num of ah: " + helperNodes.size());

		
			if (netSize > infectedNodes + delta) {
				// add a new ah
				trigger(new NeedHelper(), sourcePort);
			} else if (netSize <= infectedNodes + delta && netSize >= infectedNodes + delta -  Configuration.ACTIVE_HELPER_FANOUT * ahMinSize) {
				// do nothing
			} else {
				// remove a helper
				if (activeHelpers.size() > 0) {
					MSPeerAddress helper = activeHelpers.getLast();
					trigger(new DontNeedHelper(helper), sourcePort);
					activeHelpers.remove(helper);
					helperNodes.remove(helper);
				}
			}
		}
	};

//-------------------------------------------------------------------	
	Handler<GenerateStream> handleGenerateStream = new Handler<GenerateStream>() {
		public void handle(GenerateStream event) {
			buffer.addBlock(newBlock);
			buffer.playNext();

			if (passiveHelper != null)
				trigger(new BlockData(msPeerSelf, passiveHelper, newBlock, blockSize), messagePort);
			
			if (nodes.size() < Configuration.SRC_FANOUT - helperChildren.length) {
				for (MSPeerDescriptor desc : randomView.getAll()) {
					MSPeerAddress node = desc.getMSPeerAddress();
					if (!nodes.contains(node)) {
						nodes.add(node);
						fdRegister(node);
					}
				}
			}	
			
			for (MSPeerAddress node : nodes) {
				trigger(new BlockData(msPeerSelf, node, newBlock, blockSize), messagePort);
				Snapshot.updateLoad(msPeerSelf);
			}
			
			for (int i = 0; i < Configuration.MAX_HELPER_CHILDREN; i++) {
				if (helperChildren[i] != null)
					trigger(new BlockData(msPeerSelf, helperChildren[i], newBlock, blockSize), messagePort);
			}

			newBlock++;

			Snapshot.updateBuffer(msPeerSelf, buffer);
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
			MSComponents component = event.getComponent();
			
			if (component == MSComponents.Partnership)			
				trigger(event, partnership.getNegative(MessagePort.class));
			else
				trigger(event, membership.getNegative(MessagePort.class));
		}
	};

//-------------------------------------------------------------------	
	Handler<AddHelper> handleAddHelper = new Handler<AddHelper>() {
		public void handle(AddHelper event) {			
			MSPeerAddress helper = event.getHelperAddress();
			HelperType helperType = event.getHelperType();
			
			if (helperType == HelperType.Passive)
				passiveHelper = helper;
			else
				activeHelpers.add(helper);
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
	Handler<RegisterHelperNodes> handleRegisterHelperNodes = new Handler<RegisterHelperNodes>() {
		public void handle(RegisterHelperNodes event) {
			helperNodes.put(event.getMSPeerSource(), event.getNodes());
		}
	};

//-------------------------------------------------------------------	
	Handler<RequestHelperNodes> handleRequestHelperNodes = new Handler<RequestHelperNodes>() {
		public void handle(RequestHelperNodes event) {
			ArrayList<MSPeerAddress> nodes = new ArrayList<MSPeerAddress>();
			
			for (MSPeerAddress helper : helperNodes.keySet()) {
				if (!helper.equals(event.getMSPeerSource()))
					nodes.addAll(helperNodes.get(helper));
			}
			
			trigger(new ResponseHelperNodes(msPeerSelf, event.getMSPeerSource(), nodes), messagePort);
		}
	};

//-------------------------------------------------------------------	
	Handler<DistributionTimeout> handleDistributionTimeout = new Handler<DistributionTimeout>() {
		public void handle(DistributionTimeout event) {
			double[] total = new double[Configuration.SLOT_RANGE];
			int count = distributionHistorty.size();
			
			for (int i = 0; i < 11; i++) {
				total[i] = 0;
				bwDistribution[i] = 0;
			}
				
			for (double[] d : distributionHistorty) {
				
				for (int i = 0; i < 11; i++)
					total[i] += d[i];
			}
			
			for (int i = 0; i < 11; i++)
				bwDistribution[i] = total[i] / count;

			double s = 0;
			for (Double size : netSizeHistorty)
				s += size;
			
			if (s == 0)
				netSize = 0;
			else
				netSize = s / netSizeHistorty.size();

			Snapshot.updateNetSize(netSize);
						
			for (MSPeerAddress node : nodes)
				trigger(new DistributionRequest(msPeerSelf, node), messagePort);
			
			distributionHistorty.clear();
			netSizeHistorty.clear();			
		}
	};

//-------------------------------------------------------------------	
	Handler<DistributionResponse> handleDistributionResponse = new Handler<DistributionResponse>() {
		public void handle(DistributionResponse event) {
			distributionHistorty.add(event.getDistribution());
			netSizeHistorty.add(event.getNetSize());
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

//-------------------------------------------------------------------	
	private int calcInfectedNodes(double[] bwDist, int tqos) {
		double cum = 0;
		double random = rnd.nextDouble();
		int slot = 0;
		int size = 0;
		
		for (int i = 0; i < Configuration.SLOT_RANGE; i++) {
			cum += bwDist[i];
			if (cum >= random) {
				slot = i;
				break;
			}			
		}
		
		if (tqos > TD) {
			size += slot;
			for (int i = 0; i < slot; i++) 
				size += calcInfectedNodes(bwDist, tqos - TD);
		}
		
		return size;
	}

}
