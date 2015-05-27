package clive.peer.partnership;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;

public class SendBM extends Timeout {

	public SendBM(SchedulePeriodicTimeout request) {
		super(request);
	}
}
