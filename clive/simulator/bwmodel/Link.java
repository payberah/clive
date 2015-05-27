package clive.simulator.bwmodel;

import clive.peer.common.MSMessage;

public final class Link {

	// link capacities are given in bytes per second
	private final long capacity;
	private long lastExitTime;

//-------------------------------------------------------------------	
	public Link(long capacity) {
		this.capacity = capacity;
		this.lastExitTime = System.currentTimeMillis();
	}

//-------------------------------------------------------------------	
	public long addMessage(MSMessage message) {
		double size = message.getSize();
		double capacityPerMs = ((double) capacity) / 1000;
		long bwDelayMs = (long) (size / capacityPerMs);
		long now = System.currentTimeMillis();

		if (now >= lastExitTime) {
			// the pipe is empty
			lastExitTime = now + bwDelayMs;
		} else {
			// the pipe has some messages and the last message's exit time is
			// stored in lastExitTime
			lastExitTime = lastExitTime + bwDelayMs;
			//
			bwDelayMs = lastExitTime - now;
		}
		
		return bwDelayMs;
	}
}
