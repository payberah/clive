package clive.peer.source;

import clive.peer.common.MSPeerAddress;

import se.sics.kompics.Event;

public class RemoveHelper extends Event {

	private final MSPeerAddress address;

//-------------------------------------------------------------------
	public RemoveHelper(MSPeerAddress address) {
		this.address = address;
	}

//-------------------------------------------------------------------
	public MSPeerAddress getHelperAddress() {
		return this.address;
	}
}
