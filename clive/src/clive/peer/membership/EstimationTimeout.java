package clive.peer.membership;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class EstimationTimeout extends Timeout {

	public EstimationTimeout(SchedulePeriodicTimeout request) {
		super(request);
	}

//-------------------------------------------------------------------
	public EstimationTimeout(ScheduleTimeout request) {
		super(request);
	}
}
