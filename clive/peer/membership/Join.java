package clive.peer.membership;


import java.util.LinkedList;

import clive.peer.common.MSPeerAddress;

import se.sics.kompics.Event;

public final class Join extends Event {

	private final MSPeerAddress self;
	private final LinkedList<MSPeerAddress> cyclonInsiders;

//-------------------------------------------------------------------
	public Join(MSPeerAddress self, LinkedList<MSPeerAddress> cyclonInsiders) {
		super();
		this.self = self;
		this.cyclonInsiders = cyclonInsiders;
	}

//-------------------------------------------------------------------
	public final MSPeerAddress getSelf() {
		return self;
	}

//-------------------------------------------------------------------
	public LinkedList<MSPeerAddress> getCyclonInsiders() {
		return cyclonInsiders;
	}

//-------------------------------------------------------------------
	@Override
	public String toString() {
		return "Join(" + self + ", " + cyclonInsiders + ")";
	}
}
