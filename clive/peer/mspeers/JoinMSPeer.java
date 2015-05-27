package clive.peer.mspeers;

import java.math.BigInteger;

import se.sics.kompics.Event;

public class JoinMSPeer extends Event {

	private final BigInteger msPeerId;

//-------------------------------------------------------------------
	public JoinMSPeer(BigInteger msPeerId) {
		this.msPeerId = msPeerId;
	}

//-------------------------------------------------------------------
	public BigInteger getMSPeerId() {
		return this.msPeerId;
	}
}
