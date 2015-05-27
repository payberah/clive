package clive.peer.membership;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class HitsTimeout extends Timeout {

	public HitsTimeout(SchedulePeriodicTimeout request) {
		super(request);
	}

//-------------------------------------------------------------------
	public HitsTimeout(ScheduleTimeout request) {
		super(request);
	}
}
