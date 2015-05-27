package clive.peer.activehelper;

import java.math.BigInteger;

import se.sics.kompics.Event;

public class JoinActiveHelper extends Event {

	private final BigInteger msPeerId;

//-------------------------------------------------------------------
	public JoinActiveHelper(BigInteger msPeerId) {
		this.msPeerId = msPeerId;
	}

//-------------------------------------------------------------------
	public BigInteger getMSPeerId() {
		return this.msPeerId;
	}
}
