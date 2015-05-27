package clive.peer.passivehelper;

import java.math.BigInteger;

import se.sics.kompics.Event;

public class JoinPassiveHelper extends Event {

	private final BigInteger msPeerId;

//-------------------------------------------------------------------
	public JoinPassiveHelper(BigInteger msPeerId) {
		this.msPeerId = msPeerId;
	}

//-------------------------------------------------------------------
	public BigInteger getMSPeerId() {
		return this.msPeerId;
	}
}
