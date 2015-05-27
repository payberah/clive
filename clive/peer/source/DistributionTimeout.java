package clive.peer.source;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;

public class DistributionTimeout extends Timeout {

	public DistributionTimeout(SchedulePeriodicTimeout request) {
		super(request);
	}
}
