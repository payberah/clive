package clive.peer.partnership;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;

public class PlayStream extends Timeout {

	public PlayStream(SchedulePeriodicTimeout request) {
		super(request);
	}
}
