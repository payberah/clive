package clive.peer.passivehelper;

import clive.main.Configuration;
import clive.peer.common.BlockData;
import clive.peer.common.Buffer;
import clive.peer.common.MSPeerAddress;
import clive.peer.mspeers.MessagePort;
import clive.peer.partnership.BlockRequest;
import clive.simulator.snapshot.Snapshot;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

public final class PassiveHelper extends ComponentDefinition {
	
	Negative<PassiveHelperPort> helperPort = negative(PassiveHelperPort.class);
	
	Positive<MessagePort> messagePort = positive(MessagePort.class);
	Positive<Network> network = positive(Network.class);
	Positive<Timer> timer = positive(Timer.class);

	private static int BUFFER_SIZE = 2000;
	private MSPeerAddress msPeerSelf;

	private Buffer buffer;
	
	private int blockSize;
	private int load = 0;
	
//-------------------------------------------------------------------	
	public PassiveHelper() {
		//connect(network, membership.getNegative(Network.class));
		//connect(network, partnership.getNegative(Network.class));
		//connect(network, streaming.getNegative(Network.class));
		
		subscribe(handleInit, control);
		subscribe(handleJoin, helperPort);
		subscribe(handleBlockRequest, messagePort);
		subscribe(handleBlockData, messagePort);

	}

//-------------------------------------------------------------------	
	Handler<PassiveHelperInit> handleInit = new Handler<PassiveHelperInit>() {
		public void handle(PassiveHelperInit init) {
			msPeerSelf = init.getMSPeerSelf();
			
			blockSize = init.getMSConfiguration().getBlockSize();			
			buffer = new Buffer(BUFFER_SIZE, Configuration.WINDOW_SIZE);
		}
	};

//-------------------------------------------------------------------	
	Handler<JoinPassiveHelper> handleJoin = new Handler<JoinPassiveHelper>() {
		public void handle(JoinPassiveHelper event) {
			//Snapshot.addPeer(msPeerSelf, uploadSlots, buffer, System.currentTimeMillis());
		}
	};

//-------------------------------------------------------------------	
	Handler<BlockRequest> handleBlockRequest = new Handler<BlockRequest>() {
		public void handle(BlockRequest event) {
			long blockIndex = event.getBlockIndex();
			
			if (buffer.contains(blockIndex)) {
				load++;
				trigger(new BlockData(msPeerSelf, event.getMSPeerSource(), blockIndex, blockSize), messagePort);
				Snapshot.updateHelperLoad(load);
			} 
		}
	};
	
//-------------------------------------------------------------------	
	Handler<BlockData> handleBlockData = new Handler<BlockData>() {
		public void handle(BlockData event) {
			
			long blockIndex = event.getBlockIndex();
			buffer.addBlock(blockIndex);
			Snapshot.updateBuffer(msPeerSelf, buffer);
		}
	};
}
