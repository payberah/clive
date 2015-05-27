package clive.simulator.bwmodel;

import clive.peer.common.MSMessage;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class BwDelayedMessage extends Timeout {

	private final MSMessage message;
	private final boolean beingSent;

//-------------------------------------------------------------------	
	public BwDelayedMessage(ScheduleTimeout st, MSMessage message, boolean beingSent) {
		super(st);
		this.message = message;
		this.beingSent = beingSent;
	}

//-------------------------------------------------------------------	
	public MSMessage getMessage() {
		return message;
	}

//-------------------------------------------------------------------	
	public boolean isBeingSent() {
		return beingSent;
	}
}
