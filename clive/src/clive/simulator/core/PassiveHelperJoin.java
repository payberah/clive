package clive.simulator.core;


import java.math.BigInteger;

import clive.main.Configuration;

import se.sics.kompics.Event;

public final class PassiveHelperJoin extends Event {

	private final BigInteger passiveHelperId;

//-------------------------------------------------------------------	
	public PassiveHelperJoin() {
		this.passiveHelperId = Configuration.PASSIVE_HELPER_ID;
	}

//-------------------------------------------------------------------	
	public BigInteger getMSPeerId() {
		return passiveHelperId;
	}
}
