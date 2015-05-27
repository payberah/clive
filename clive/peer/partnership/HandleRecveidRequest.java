package clive.peer.partnership;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;

public class HandleRecveidRequest extends Timeout {

	public HandleRecveidRequest(SchedulePeriodicTimeout request) {
		super(request);
	}
}
