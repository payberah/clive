package clive.simulator.core;

import java.math.BigInteger;

import se.sics.kompics.Event;

public final class MSZeroJoin extends Event {

	private final int bw;
	private final int seats;
	private final int agents;
	private final BigInteger msPeerId;

//-------------------------------------------------------------------	
	public MSZeroJoin(BigInteger msPeerId, int seats, int agents) {
		this.msPeerId = msPeerId;
		this.bw = 0;
		this.seats = seats;
		this.agents = agents;
	}

//-------------------------------------------------------------------	
	public BigInteger getMSPeerId() {
		return msPeerId;
	}

//-------------------------------------------------------------------	
	public int getBw() {
		return this.bw;
	}

//-------------------------------------------------------------------	
	public int getSeats() {
		return this.seats;
	}

//-------------------------------------------------------------------	
	public int getAgents() {
		return this.agents;
	}
	
//-------------------------------------------------------------------	
	@Override
	public String toString() {
		return "Join@" + this.msPeerId + "(" + this.agents +", " + this.seats + ")";
	}
}
