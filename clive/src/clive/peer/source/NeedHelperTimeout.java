package clive.peer.source;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;

public class NeedHelperTimeout extends Timeout {

	public NeedHelperTimeout(SchedulePeriodicTimeout request) {
		super(request);
	}
}
