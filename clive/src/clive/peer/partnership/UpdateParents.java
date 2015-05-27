package clive.peer.partnership;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;

public class UpdateParents extends Timeout {

	public UpdateParents(SchedulePeriodicTimeout request) {
		super(request);
	}
}
