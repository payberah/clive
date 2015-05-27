package clive.peer.partnership;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;

public class RequestBlocks extends Timeout {

	public RequestBlocks(SchedulePeriodicTimeout request) {
		super(request);
	}
}
