package clive.peer.membership;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import clive.main.Configuration;
import clive.peer.common.MSPeerAddress;
import clive.peer.common.MSPeerDescriptor;
import clive.peer.common.RandomView;
import clive.peer.mspeers.MessagePort;
import clive.peer.source.DistributionResponse;
import clive.peer.source.DistributionRequest;
import clive.simulator.snapshot.FileIO;
import clive.simulator.snapshot.Snapshot;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public final class Gradient extends ComponentDefinition {
	Negative<GradientPeerSampling> gradientPort = negative(GradientPeerSampling.class);

	Positive<MessagePort> messagePort = positive(MessagePort.class);
	Positive<Timer> timerPort = positive(Timer.class);

	private MSPeerAddress self;

	private int shuffleLength;
	private long shufflePeriod;
	private long shuffleTimeout;

	private RandomView randomView;
	
	private int slots;
	
	private boolean joining;

	private HashMap<UUID, MSPeerAddress> outstandingShuffles;
	
	//private AvgData avgData;
	private boolean lock = false;
	private double[] hits = new double[Configuration.SLOT_RANGE];
	private double[] hitRatio = new double[Configuration.SLOT_RANGE];
	private double netSize = 0;
	private int timestamp = 0;
	private double estimatedNetSize = 0;

//-------------------------------------------------------------------	
	public Gradient() {
		outstandingShuffles = new HashMap<UUID, MSPeerAddress>();

		subscribe(handleInit, control);
		subscribe(handleJoin, gradientPort);
		subscribe(handleInitiateShuffle, timerPort);
		subscribe(handleRandomShuffleTimeout, timerPort);
		subscribe(handleEstimationTimeout, timerPort);
		
		subscribe(handleRandomShuffleRequest, messagePort);
		subscribe(handleRandomShuffleResponse, messagePort);
		subscribe(handleDistributionRequest, messagePort);
		subscribe(handleResetBroadcast, messagePort);
	}

//-------------------------------------------------------------------	
	Handler<GradientInit> handleInit = new Handler<GradientInit>() {
		public void handle(GradientInit init) {
			shuffleLength = init.getConfiguration().getShuffleLength();
			shufflePeriod = init.getConfiguration().getShufflePeriod();
			shuffleTimeout = init.getConfiguration().getShuffleTimeout();
			randomView = init.getRandomView();
			slots = init.getSlots();
			
			for (int i = 0; i < Configuration.SLOT_RANGE; i++) {
				hits[i] = 0;
				hitRatio[i] = 0;
			}		
		}
	};

//-------------------------------------------------------------------	
	Handler<Join> handleJoin = new Handler<Join>() {
		public void handle(Join event) {
			self = event.getSelf();

			if (self.getPeerId().equals(Configuration.SOURCE_ID)) {
				SchedulePeriodicTimeout rt = new SchedulePeriodicTimeout(60000, 60000);
				rt.setTimeoutEvent(new EstimationTimeout(rt));
				trigger(rt, timerPort);

				netSize = 1;
			}

			if (slots <= Configuration.SLOT_RANGE)
				hits[slots] = 1;
			
			LinkedList<MSPeerAddress> insiders = event.getCyclonInsiders();

			//Snapshot.updateNum(self, num);
			
			if (insiders.size() == 0) {
				// I am the first peer
				trigger(new JoinCompleted(self), gradientPort);

				// schedule shuffling
				SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(shufflePeriod, shufflePeriod);
				spt.setTimeoutEvent(new InitiateShuffle(spt));
				trigger(spt, timerPort);
				return;
			}

			MSPeerAddress peer = insiders.poll();
			initiateShuffle(1, peer);
			joining = true;
		}
	};

//-------------------------------------------------------------------	
	private void initiateShuffle(int shuffleSize, MSPeerAddress randomPeer) {
		// send the random view to a random peer
		ArrayList<MSPeerDescriptor> randomDescriptors = randomView.selectToSendAtActive(shuffleSize - 1, randomPeer);
		randomDescriptors.add(new MSPeerDescriptor(self, slots));
		DescriptorBuffer randomBuffer = new DescriptorBuffer(self, randomDescriptors);
		
		ScheduleTimeout rst = new ScheduleTimeout(shuffleTimeout);
		rst.setTimeoutEvent(new ShuffleTimeout(rst, randomPeer));
		UUID rTimeoutId = rst.getTimeoutEvent().getTimeoutId();

		lock = true;
		findLocalDistribution();
		outstandingShuffles.put(rTimeoutId, randomPeer);
		double[] nodeRatio = new double[Configuration.SLOT_RANGE];
		for (int i = 0; i < Configuration.SLOT_RANGE; i++)
			nodeRatio[i] = hitRatio[i];
		
		ShuffleRequest rRequest = new ShuffleRequest(self, randomPeer, rTimeoutId, randomBuffer, nodeRatio, netSize, timestamp);

		trigger(rst, timerPort);
		trigger(rRequest, messagePort);
		
		Snapshot.updateNum(self, hitRatio);
	}

//-------------------------------------------------------------------	
	Handler<InitiateShuffle> handleInitiateShuffle = new Handler<InitiateShuffle>() {
		public void handle(InitiateShuffle event) {
			randomView.incrementDescriptorAges();
			
			MSPeerAddress randomPeer = randomView.selectPeerToShuffleWith();
			
			if (randomPeer != null)
				initiateShuffle(shuffleLength, randomPeer);			
		}
	};

//-------------------------------------------------------------------	
	Handler<ShuffleRequest> handleRandomShuffleRequest = new Handler<ShuffleRequest>() {
		public void handle(ShuffleRequest event) {
			MSPeerAddress peer = event.getMSPeerSource();
			DescriptorBuffer receivedRandomBuffer = event.getRandomBuffer();
			DescriptorBuffer toSendRandomBuffer = new DescriptorBuffer(self, randomView.selectToSendAtPassive(receivedRandomBuffer.getSize(), peer));
			randomView.selectToKeep(peer, receivedRandomBuffer.getDescriptors());

			if (peer == null)
				return;
			
			double[] peerHitRatio = event.getHitRatio();
			double peerNetSize = event.getNetSize();
			int peerTimestamp = event.getTimestamp();
			double returnNetSize = peerNetSize;

			double[] nodeRatio = new double[Configuration.SLOT_RANGE];
			if (!lock) {
				findHitsDistribution(peerHitRatio);
				for (int i = 0; i < Configuration.SLOT_RANGE; i++)
					nodeRatio[i] = hitRatio[i];
				
				if (timestamp == peerTimestamp) {
					netSize = (netSize + peerNetSize) / 2;
					returnNetSize = netSize;
				}			
			} else {
				for (int i = 0; i < Configuration.SLOT_RANGE; i++)
					nodeRatio[i] = peerHitRatio[i];
			}
			

			try {
			ShuffleResponse response = new ShuffleResponse(self, peer, event.getRequestId(), toSendRandomBuffer, nodeRatio, returnNetSize, timestamp);
			trigger(response, messagePort);
			} catch (Exception e) {
				System.err.println("====> self: " + self + ", peer: " + peer);
				FileIO.append("====> self: " + self + ", peer: " + peer + "\n", "OOO");
			}
		}
	};

//-------------------------------------------------------------------	
	Handler<ShuffleResponse> handleRandomShuffleResponse = new Handler<ShuffleResponse>() {
		public void handle(ShuffleResponse event) {
			if (joining) {
				joining = false;
				trigger(new JoinCompleted(self), gradientPort);

				// schedule shuffling
				SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(shufflePeriod, shufflePeriod);
				spt.setTimeoutEvent(new InitiateShuffle(spt));
				trigger(spt, timerPort);
			}

			// cancel shuffle timeout
			UUID shuffleId = event.getRequestId();
			if (outstandingShuffles.containsKey(shuffleId)) {
				outstandingShuffles.remove(shuffleId);
				CancelTimeout ct = new CancelTimeout(shuffleId);
				trigger(ct, timerPort);
			}

			MSPeerAddress peer = event.getMSPeerSource();
			DescriptorBuffer receivedRandomBuffer = event.getRandomBuffer();
			randomView.selectToKeep(peer, receivedRandomBuffer.getDescriptors());			
			
			double[] peerHitRatio = event.getHitRatio();
			for (int i = 0; i < Configuration.SLOT_RANGE; i++)
				hitRatio[i] = peerHitRatio[i];

			double peerNetSize = event.getNetSize();
			int peerTimestamp = event.getTimestamp();
			
			if (timestamp == peerTimestamp)
				netSize = peerNetSize;
			
			lock = false;
		}
	};

//-------------------------------------------------------------------	
	Handler<ShuffleTimeout> handleRandomShuffleTimeout = new Handler<ShuffleTimeout>() {
		public void handle(ShuffleTimeout event) {
			//logger.warn("SHUFFLE TIMED OUT");
		}
	};

//-------------------------------------------------------------------	
	Handler<EstimationTimeout> handleEstimationTimeout = new Handler<EstimationTimeout>() {
		public void handle(EstimationTimeout event) {
			if (self.getPeerId().equals(Configuration.SOURCE_ID)) {
				estimatedNetSize = netSize;
				System.out.println("===================================> " + 1 / estimatedNetSize);
				DecimalFormat df = new DecimalFormat("#.##");
				String str = "source distribution: "; 
				for (int i = 0; i < 11; i++)
					str += df.format(hitRatio[i]) + ", ";
				System.out.println(str);
				
				timestamp++;
				netSize = 1;
	
				for (int i = 0; i < Configuration.SLOT_RANGE; i++) {
					hits[i] = 0;
					hitRatio[i] = 0;
				}
					
				for (MSPeerDescriptor desc : randomView.getAll())
					trigger(new ResetBroadcast(self, desc.getMSPeerAddress(), timestamp), messagePort);
			}
		}
	};

//-------------------------------------------------------------------	
	Handler<ResetBroadcast> handleResetBroadcast = new Handler<ResetBroadcast>() {
		public void handle(ResetBroadcast event) {
			int peerTimestamp = event.getEpoch();
			
			if (timestamp < peerTimestamp) {
				timestamp = peerTimestamp;
				estimatedNetSize = netSize;
				netSize = 0;

				for (int i = 0; i < Configuration.SLOT_RANGE; i++) {
					hits[i] = 0;
					hitRatio[i] = 0;
				}

				if (slots <= Configuration.SLOT_RANGE)
					hits[slots] = 1;

				for (MSPeerDescriptor desc : randomView.getAll())
					trigger(new ResetBroadcast(self, desc.getMSPeerAddress(), timestamp), messagePort);
			}
		}
	};

//-------------------------------------------------------------------	
	Handler<HitsTimeout> handleHitsTimeout = new Handler<HitsTimeout>() {
		public void handle(HitsTimeout event) {
			findLocalDistribution();
			
			for (int i = 0; i < Configuration.SLOT_RANGE; i++)
				hits[i] = 0;

			hits[slots] = 1;
		}
	};

//-------------------------------------------------------------------	
	Handler<DistributionRequest> handleDistributionRequest = new Handler<DistributionRequest>() {
		public void handle(DistributionRequest event) {
			trigger(new DistributionResponse(self, event.getMSPeerSource(), hitRatio, Snapshot.getNetSize()), messagePort);
		}
	};

	
//-------------------------------------------------------------------	
    private void findLocalDistribution() {
		double count = 0;
		
		for (int i = 0; i < Configuration.SLOT_RANGE; i++)
			count += hits[i];
		
		
		if (count > 0) {
			for (int i = 0; i < Configuration.SLOT_RANGE; i++)
				hitRatio[i] = hits[i] / count;
		}
		
		Snapshot.updateNum(self, hitRatio);
    }

//-------------------------------------------------------------------	
    private void findHitsDistribution(double[] peerHitRatio) {
		for (int i = 0; i < Configuration.SLOT_RANGE; i++)
			hitRatio[i] = (hitRatio[i] + peerHitRatio[i]) / 2;
    }
}
