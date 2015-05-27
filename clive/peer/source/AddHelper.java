package clive.peer.source;

import clive.peer.common.HelperType;
import clive.peer.common.MSPeerAddress;

import se.sics.kompics.Event;

public class AddHelper extends Event {

	private final MSPeerAddress address;
	private final HelperType type;

//-------------------------------------------------------------------
	public AddHelper(MSPeerAddress address, HelperType type) {
		this.address = address;
		this.type = type;
	}

//-------------------------------------------------------------------
	public MSPeerAddress getHelperAddress() {
		return this.address;
	}

//-------------------------------------------------------------------
	public HelperType getHelperType() {
		return this.type;
	}
}
