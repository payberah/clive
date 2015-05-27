package clive.peer.membership;

import clive.peer.common.MSPeerAddress;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class ShuffleTimeout extends Timeout {

	private final MSPeerAddress peer;

//-------------------------------------------------------------------
	public ShuffleTimeout(ScheduleTimeout request, MSPeerAddress peer) {
		super(request);
		this.peer = peer;
	}

//-------------------------------------------------------------------
	public MSPeerAddress getPeer() {
		return peer;
	}
}
