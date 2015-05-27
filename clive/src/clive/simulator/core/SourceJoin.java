package clive.simulator.core;


import java.math.BigInteger;

import clive.main.Configuration;

import se.sics.kompics.Event;

public final class SourceJoin extends Event {

	private final int seats;
	private final int agents;
	private final BigInteger sourceId;

//-------------------------------------------------------------------	
	public SourceJoin(Long seats) {
		this.sourceId = Configuration.SOURCE_ID;
		this.seats = seats.intValue();
		this.agents = 0;
	}

//-------------------------------------------------------------------	
	public BigInteger getMSPeerId() {
		return sourceId;
	}

//-------------------------------------------------------------------	
	public int getSeats() {
		return this.seats;
	}
	
//-------------------------------------------------------------------	
	@Override
	public String toString() {
		return "Source Join@" + this.sourceId + "(" + this.agents +", " + this.seats + ")";
	}
}
