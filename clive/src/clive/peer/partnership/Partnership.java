package clive.peer.partnership;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;

import clive.main.Configuration;
import clive.peer.common.BlockData;
import clive.peer.common.Buffer;
import clive.peer.common.MSPeerAddress;
import clive.peer.common.MSPeerDescriptor;
import clive.peer.common.RandomView;
import clive.peer.mspeers.MessagePort;
import clive.simulator.snapshot.Snapshot;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.p2p.fd.FailureDetector;
import se.sics.kompics.p2p.fd.PeerFailureSuspicion;
import se.sics.kompics.p2p.fd.StartProbingPeer;
import se.sics.kompics.p2p.fd.StopProbingPeer;
import se.sics.kompics.p2p.fd.SuspicionStatus;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timer;

public final class Partnership extends ComponentDefinition {

	Negative<PartnershipPort> partnershipPort = negative(PartnershipPort.class);
			
	Positive<MessagePort> messagePort = positive(MessagePort.class);
	Positive<Timer> timer = positive(Timer.class);
	Positive<FailureDetector> epfd = positive(FailureDetector.class);

	private int seed = 0;
	private long drift = 25;
	private int windowSize = 25;
	private int highPrioritySize = 25;
	
	private MSPeerAddress self;
	private Buffer buffer;
	private int money;
	private long load = 0;
	private Random random;
	private int blockSize;
	private int bufferingTime;
	private boolean hasReceivedBlock = false;
	private boolean hasReceivedBM = false;
	private long sendBMPeriod;
	private long blockPlayingPeriod;
	private long updateParentsPeriod;
	private long requestBlockPeriod;
	private TreeSet<Long> requestedBlocks = new TreeSet<Long>();
	private TreeSet<Long> requestedBlocksFromHelper = new TreeSet<Long>();
	
	private RandomView randomView;
	private Children children;
	private Parents parents;
	private MSPeerAddress helper;

	private HashMap<Address, UUID> fdRequests = new HashMap<Address, UUID>();
	private HashMap<Address, MSPeerAddress> fdChildren = new HashMap<Address, MSPeerAddress>();
	private HashMap<Address, MSPeerAddress> fdParents = new HashMap<Address, MSPeerAddress>();
	private enum Relation {Parent, Child};
	
	private TreeMap<Long, ArrayList<MSPeerAddress>> receivedReq = new TreeMap<Long, ArrayList<MSPeerAddress>>();
	
//-------------------------------------------------------------------	
	public Partnership() {
		subscribe(handleInit, control);
		subscribe(handleStartPartnership, partnershipPort);
		subscribe(handleUpdateParents, timer);
		subscribe(handleSendBM, timer);
		subscribe(handleRequestBlocks, timer);
		subscribe(handlePlayStream, timer);
		subscribe(handleReceivedRequests, timer);
		subscribe(handleMarketMsgRequest, messagePort);
		subscribe(handleMarketMsgResponse, messagePort);
		subscribe(handleBM, messagePort);
		subscribe(handleBlockRequest, messagePort);
		subscribe(handleBlockRequestNack, messagePort);
		subscribe(handleBlockData, messagePort);
		subscribe(handlePeerFailureSuspicion, epfd);
	}

//-------------------------------------------------------------------	
	Handler<PartnershipInit> handleInit = new Handler<PartnershipInit>() {
		public void handle(PartnershipInit init) {
			self = init.getSelf();
			randomView = init.getRandomView();
			money = init.getUploadSlots();
			buffer = init.getBuffer();
			blockSize = init.getBlockSize();
			bufferingTime = init.getBufferingTime();
			sendBMPeriod = init.getSendBMPeriod();
			requestBlockPeriod = init.getRequestBlockPeriod();
			updateParentsPeriod = init.getUpdateParentsPeriod();			
			helper = init.getHelperAddress(); 
			children = new Children(money);
			parents = new Parents(Configuration.PARENT_SIZE, drift);
			random = new Random(seed);
			blockPlayingPeriod = (long)(((blockSize * 1000) / init.getStreamRate()) * Configuration.STRIPES);
		}
	};

//-------------------------------------------------------------------	
	Handler<StartPartnership> handleStartPartnership = new Handler<StartPartnership>() {
		public void handle(StartPartnership event) {
			if (self.getPeerId().equals(Configuration.SOURCE_ID))
				return;
			
			SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(updateParentsPeriod, updateParentsPeriod);
			spt.setTimeoutEvent(new UpdateParents(spt));
			trigger(spt, timer);

			SchedulePeriodicTimeout bmt = new SchedulePeriodicTimeout(sendBMPeriod, sendBMPeriod);
			bmt.setTimeoutEvent(new SendBM(bmt));
			trigger(bmt, timer);

			SchedulePeriodicTimeout hr = new SchedulePeriodicTimeout(1000, 1000);
			hr.setTimeoutEvent(new HandleRecveidRequest(hr));
			trigger(hr, timer);
		}
	};
	
//-------------------------------------------------------------------	
	Handler<UpdateParents> handleUpdateParents = new Handler<UpdateParents>() {
		public void handle(UpdateParents event) {
			ArrayList<MSPeerDescriptor> randomDescriptors = randomView.getAll();
			ArrayList<MSPeerDescriptor> partners = new ArrayList<MSPeerDescriptor>();

			for (MSPeerDescriptor descriptor : randomDescriptors) {
				if (!partners.contains(descriptor.getMSPeerAddress()))
					partners.add(descriptor);
			}

			MSPeerAddress partnerAddress;
			long pp = buffer.getPlaybackPoint();
			ArrayList<Long> bm = buffer.getBM();
			ArrayList<MSPeerAddress> currentChildren = new ArrayList<MSPeerAddress>(children.getChildren());
			if (parents.hasFreeSlots()) {
				for (MSPeerDescriptor partner : partners) {
					partnerAddress = partner.getMSPeerAddress();
					if (partnerAddress.getPeerId().equals(Configuration.SOURCE_ID) || parents.contains(partnerAddress))
						continue;
					trigger(new MarketMsgRequest(self, partnerAddress, money, pp, bm, currentChildren, RegisterRequest.REGISTER), messagePort);
				}
			}
		}
	};

//-------------------------------------------------------------------	
	Handler<MarketMsgRequest> handleMarketMsgRequest = new Handler<MarketMsgRequest>() {
		public void handle(MarketMsgRequest event) {
			MSPeerAddress peer = event.getMSPeerSource();
			int peerMoney = event.getMoney();
			ArrayList<MSPeerAddress> peerChildren = event.getChildren();
			RegisterRequest peerRequest = event.getRequest();
			
			if (money == 0)
				return;
			
			if (peerRequest == RegisterRequest.REGISTER && !children.contains(peer)) {
				if (children.hasFreeSlots()) {
					registerChild(peer, peerMoney, peerChildren);
					trigger(new MarketMsgResponse(self, peer, money, load, buffer.getBM(), RegisterResponse.ACCEPTED), messagePort);
				} else {
					trigger(new MarketMsgResponse(self, peer, money, load, null, RegisterResponse.DENIED), messagePort);
				}
			} else if (peerRequest == RegisterRequest.UNREGISTER) {
				unregisterChild(peer);				
			}
			
			Snapshot.updateChildren(self, children.getChildren());
		}
	};

//-------------------------------------------------------------------	
	Handler<MarketMsgResponse> handleMarketMsgResponse = new Handler<MarketMsgResponse>() {
		public void handle(MarketMsgResponse event) {
			MSPeerAddress peer = event.getMSPeerSource();
			int peerMoney = event.getMoney();
			long peerLoad = event.getLoad();
			ArrayList<Long> peerBM = event.getBM();
			RegisterResponse response = event.getResponse();
			
			if (response == RegisterResponse.ACCEPTED) {
				if (parents.hasFreeSlots() && !parents.contains(peer))
					parents.add(peer, peerMoney, peerLoad, peerBM);
				else if (!parents.hasFreeSlots() && !parents.contains(peer)) {
					trigger(new MarketMsgRequest(self, peer, RegisterRequest.UNREGISTER), messagePort);
				}
				
				Snapshot.updateParents(self, parents.getParents());
			}
		}
	};
	
//-------------------------------------------------------------------	
	Handler<SendBM> handleSendBM = new Handler<SendBM>() {
		public void handle(SendBM event) {
			ArrayList<Long> bm = buffer.getBM();
			
			if (bm.size() > 0) {
				for (MSPeerAddress child : children.getChildren())
					trigger(new BMMessage(self, child, load, bm), messagePort);
			}
		}
	};

//-------------------------------------------------------------------	
	Handler<BMMessage> handleBM = new Handler<BMMessage>() {
		public void handle(BMMessage event) {
			MSPeerAddress peer = event.getMSPeerSource();
			long peerLoad = event.getLoad();
			ArrayList<Long> peerBM = event.getBM();
			
			parents.update(peer, peerLoad, peerBM);
			
			if (!hasReceivedBM) {
				hasReceivedBM = true;

				SchedulePeriodicTimeout rbt = new SchedulePeriodicTimeout(requestBlockPeriod, requestBlockPeriod);
				rbt.setTimeoutEvent(new RequestBlocks(rbt));
				trigger(rbt, timer);
			}
		}
	};
	
//-------------------------------------------------------------------	
	Handler<RequestBlocks> handleRequestBlocks = new Handler<RequestBlocks>() {
		public void handle(RequestBlocks event) {
			long bufferHead = buffer.head();
			long firstReqBlock = bufferHead;
			int remainingBlocks = windowSize;
			BlockHolder holder;
			
			if (bufferHead == -1 || bufferHead < parents.getSmallestBlockInPartners())
				firstReqBlock = parents.getPreferedBlockIndex();
			
			if (requestedBlocks.size() > 0 && firstReqBlock < requestedBlocks.last())
				firstReqBlock = requestedBlocks.last();
			
			if (firstReqBlock > 0) {
				long highPriorityFirstIndex = firstReqBlock + 1;
				ArrayList<BlockHolder> holders;
				HashMap<Long, ArrayList<BlockHolder>> highPriorityHolders = parents.getBlockHolders(highPriorityFirstIndex, highPriorityFirstIndex + highPrioritySize);
				
				// use the passive helper
				TreeSet<Long> helpNeededMissedBloks = buffer.getHelpNeededMissedBlocks();
				
				for (Long block : helpNeededMissedBloks) {
					if (!requestedBlocksFromHelper.contains(block)) {
						trigger(new BlockRequest(self, helper, block), messagePort);
						requestedBlocks.add(block);
						requestedBlocksFromHelper.add(block);
						highPriorityHolders.remove(block);
						remainingBlocks--;
						
						if (remainingBlocks == 0)
							break;
					}
				}
				
//				System.out.println(self + ": remaining: " + remainingBlocks + ", help: " + helpNeededMissedBloks);
//				System.out.println(self + ": head: " + bufferHead + " pp: " + buffer.getPlaybackPoint() + ", buffer: " + buffer.getBuffer());
				
				// the rest of the missed blocks
				TreeSet<Long> missedBloks = buffer.getMissedBlocks();
				if (remainingBlocks > 0) {
					for (Long block : missedBloks) {
						if (!requestedBlocks.contains(block)) {
							holders = parents.getBlockHolders(block);
							if (holders.size() > 0) {
								holder = getBestHolder(holders);
								trigger(new BlockRequest(self, holder.getAddress(), block), messagePort);
								requestedBlocks.add(block);
								highPriorityHolders.remove(block);
								remainingBlocks--;
							}
						}
					}
				}

				if (remainingBlocks > 0 && requestedBlocks.size() > 0 && bufferHead != -1 && bufferHead < requestedBlocks.first() - 1) {
					for (Long block = bufferHead; block < requestedBlocks.first(); block++) {
						holders = parents.getBlockHolders(block);
						if (holders.size() > 0) {
							holder = getBestHolder(holders);
							trigger(new BlockRequest(self, holder.getAddress(), block), messagePort);
							requestedBlocks.add(block);
							highPriorityHolders.remove(block);
							remainingBlocks--;
								
							if (remainingBlocks == 0)
								break;
						}
					}
				}

				for (int i = 0; i < remainingBlocks; i++) {
					long nextBlock = getNext(highPriorityHolders);
					holders = highPriorityHolders.get(nextBlock);

					if (nextBlock != -1 && holders.size() > 0) {
						holder = getBestHolder(holders);
						trigger(new BlockRequest(self, holder.getAddress(), nextBlock), messagePort);
						requestedBlocks.add(nextBlock);
						highPriorityHolders.remove(nextBlock);
					}
				}
			}			
		}
	};

//-------------------------------------------------------------------	
	Handler<BlockRequest> handleBlockRequest = new Handler<BlockRequest>() {
		public void handle(BlockRequest event) {
			MSPeerAddress peer = event.getMSPeerSource();
			long blockIndex = event.getBlockIndex();

			if (getRecvReqSize() + 2 > ((money * Configuration.BW_UNIT) / Configuration.BLOCK_SIZE)) {
				trigger(new BlockRequestNack(self, event.getMSPeerSource(), blockIndex), messagePort);
				return;
			}

			if (buffer.contains(blockIndex)) {
				ArrayList<MSPeerAddress> requesters;
				if (receivedReq.containsKey(blockIndex))
					requesters = receivedReq.get(blockIndex);
				else
					requesters = new ArrayList<MSPeerAddress>();
				
				requesters.add(peer);
				receivedReq.put(blockIndex, requesters);
			}
		}
	};

//-------------------------------------------------------------------	
	Handler<BlockRequestNack> handleBlockRequestNack = new Handler<BlockRequestNack>() {
		public void handle(BlockRequestNack event) {
			
			long missedBlock = event.getBlockIndex();

			requestedBlocks.remove(missedBlock);
		}
	};
	
//-------------------------------------------------------------------	
	Handler<HandleRecveidRequest> handleReceivedRequests = new Handler<HandleRecveidRequest>() {
		public void handle(HandleRecveidRequest event) {
			for (Long block : receivedReq.keySet()) {
				for (MSPeerAddress peer : receivedReq.get(block)) {
					trigger(new BlockData(self, peer, block, blockSize), messagePort);
					load++;
					Snapshot.updateLoad(self);
				}
				
			}

			receivedReq.clear();
		}
	};
	
//-------------------------------------------------------------------	
	Handler<IAmNotYourParent> handleIAmNotYourParent = new Handler<IAmNotYourParent>() {
		public void handle(IAmNotYourParent event) {
			MSPeerAddress peer = event.getMSPeerSource();

			parents.remove(peer);
			fdUnregister(peer, Relation.Parent);
			Snapshot.updateParents(self, parents.getParents());
		}
	};	

//-------------------------------------------------------------------	
	Handler<BlockData> handleBlockData = new Handler<BlockData>() {
		public void handle(BlockData event) {
			if (!hasReceivedBlock) {
				hasReceivedBlock = true;

				// schedule for play the stream after buffering 
				SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(bufferingTime, blockPlayingPeriod);
				spt.setTimeoutEvent(new PlayStream(spt));
				trigger(spt, timer);
			}
			
			long blockIndex = event.getBlockIndex();
			buffer.addBlock(blockIndex);
			requestedBlocks.remove(blockIndex);
			Snapshot.updateBuffer(self, buffer);
			
			parents.incScore(event.getMSPeerSource());
		}		
	};

//-------------------------------------------------------------------	
	Handler<PlayStream> handlePlayStream = new Handler<PlayStream>() {
		public void handle(PlayStream event) {
//			boolean result = buffer.playNext();
//			
//			Snapshot.updatePlayedBlocks(self, result);
			
			
			long head = buffer.head();
			long playbackPoint = buffer.getPlaybackPoint();
			
			boolean result = buffer.playNext();
			Snapshot.updatePlayedBlocks(self, result);

			while (head - playbackPoint > 200) {
				result = buffer.playNext();
				if (result == false)
					break;
				
				playbackPoint = buffer.getPlaybackPoint();
				Snapshot.updatePlayedBlocks(self, result);
			}	
		}
	};
	
//-------------------------------------------------------------------	
	Handler<PeerFailureSuspicion> handlePeerFailureSuspicion = new Handler<PeerFailureSuspicion>() {
		public void handle(PeerFailureSuspicion event) {
			Address suspectedPeerAddress = event.getPeerAddress();
			MSPeerAddress suspectedPeer;
			Vector<Address> allFd = new Vector<Address>();
			
			allFd.addAll(fdChildren.keySet());
			allFd.addAll(fdParents.keySet());
			
			if (event.getSuspicionStatus().equals(SuspicionStatus.SUSPECTED)) {
				if (!allFd.contains(suspectedPeerAddress) || !fdRequests.containsKey(suspectedPeerAddress))
					return;
				
				if (fdChildren.containsKey(suspectedPeerAddress)) {
					suspectedPeer = fdChildren.get(suspectedPeerAddress);
					children.remove(suspectedPeer);
					fdUnregister(suspectedPeer, Relation.Child);
				}

				if (fdParents.containsKey(suspectedPeerAddress)) {
					suspectedPeer = fdParents.get(suspectedPeerAddress);
					parents.remove(suspectedPeer);
					fdUnregister(suspectedPeer, Relation.Parent);
				}
			}
		}
	};
	


//-------------------------------------------------------------------	
	private void registerChild(MSPeerAddress peer, int peerMoney, ArrayList<MSPeerAddress> grandChildren) {
		children.add(peer, peerMoney, grandChildren);
		fdRegister(peer, Relation.Child);
	}

//-------------------------------------------------------------------	
	private void unregisterChild(MSPeerAddress peer) {
		children.remove(peer);
		fdUnregister(peer, Relation.Child);
	}

//-------------------------------------------------------------------	
	private void fdRegister(MSPeerAddress peer, Relation relation) {
		Address peerAddress = peer.getPeerAddress();
		StartProbingPeer spp = new StartProbingPeer(peerAddress, peer);
		fdRequests.put(peerAddress, spp.getRequestId());
		trigger(spp, epfd);
		
		if (relation == Relation.Child)
			fdChildren.put(peerAddress, peer);
		else
			fdParents.put(peerAddress, peer);
	}

//-------------------------------------------------------------------	
	private void fdUnregister(MSPeerAddress peer, Relation relation) {
		if (peer == null)
			return;
			
		Address peerAddress = peer.getPeerAddress();

		trigger(new StopProbingPeer(peerAddress, fdRequests.get(peerAddress)), epfd);
		fdRequests.remove(peerAddress);
		
		if (relation == Relation.Child)
			fdChildren.remove(peerAddress);
		else
			fdParents.remove(peerAddress);
	}
	
//-------------------------------------------------------------------	
	private int getRecvReqSize() {
		int num = 0;
		
		for (Long block : receivedReq.keySet())
			num += receivedReq.get(block).size();
	
		return num;
	}
	
//-------------------------------------------------------------------	
	private long getNext(HashMap<Long, ArrayList<BlockHolder>> holders) {
		long nextBlock = Long.MAX_VALUE;
		
		for (Long index : holders.keySet()) {
			if (requestedBlocks.contains(index))
				continue;
			
			if (nextBlock > index && holders.get(index).size() > 0)
				nextBlock = index;
		}
		
		if (nextBlock == Long.MAX_VALUE)
			nextBlock = -1;
		
		return nextBlock;
	}

//-------------------------------------------------------------------	
	private BlockHolder getBestHolder(ArrayList<BlockHolder> holders) {
		if (holders.size() == 0)
			return null;

		TreeSet<BlockHolder> sortedHolders = new TreeSet<BlockHolder>(holders);
		ArrayList<BlockHolder> smallestHolders = new ArrayList<BlockHolder>();

		int len = sortedHolders.size();
		int count = 0;

		if (len == 1)
			smallestHolders.add(sortedHolders.first());
		else {
			for (BlockHolder holder : sortedHolders) {
				smallestHolders.add(holder);
				count++;
				if (count == len - 1)
					break;
			}
		}
		
		int rnd = random.nextInt(smallestHolders.size());

		return smallestHolders.get(rnd);
	}
}
