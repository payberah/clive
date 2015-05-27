package clive.peer.activehelper;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;

public class UpdateNodesTimeout extends Timeout {

	public UpdateNodesTimeout(SchedulePeriodicTimeout request) {
		super(request);
	}
}
