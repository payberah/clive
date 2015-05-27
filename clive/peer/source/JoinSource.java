package clive.peer.source;

import java.math.BigInteger;

import se.sics.kompics.Event;

public class JoinSource extends Event {

	private final BigInteger msPeerId;

//-------------------------------------------------------------------
	public JoinSource(BigInteger msPeerId) {
		this.msPeerId = msPeerId;
	}

//-------------------------------------------------------------------
	public BigInteger getMSPeerId() {
		return this.msPeerId;
	}
}
