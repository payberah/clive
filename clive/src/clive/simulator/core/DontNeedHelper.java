package clive.simulator.core;

import clive.peer.common.MSPeerAddress;
import se.sics.kompics.Event;

public class DontNeedHelper extends Event {
	private final MSPeerAddress address;

//-------------------------------------------------------------------
	public DontNeedHelper(MSPeerAddress address) {
		this.address = address;
	}

//-------------------------------------------------------------------
	public MSPeerAddress getHelperAddress() {
		return this.address;
	}
}
