package clive.peer.source;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;

public class GenerateStream extends Timeout {

	public GenerateStream(SchedulePeriodicTimeout request) {
		super(request);
	}
}
