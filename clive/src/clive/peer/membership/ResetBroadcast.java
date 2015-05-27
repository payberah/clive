package clive.peer.membership;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;


public class ResetBroadcast extends MSMessage {

	private static final long serialVersionUID = 8493601671018888143L;
	private int epoch;

//-------------------------------------------------------------------
	public ResetBroadcast(MSPeerAddress source, MSPeerAddress destination, int epoch) {
		super(source, destination, MSComponents.Membership);
		this.epoch = epoch;
	}

//-------------------------------------------------------------------
	public int getEpoch() {
		return this.epoch;
	}

//-------------------------------------------------------------------
	public int getSize() {
		return 0;
	}
}
