package clive.peer.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import clive.main.Configuration;

public class Buffer {
	private final int bufferSize;
	private final int windowSize;
	private TreeSet<Long> buffer;
	private long playbackPoint;
	
//-------------------------------------------------------------------	
	public Buffer(int bufferSize, int windowSize) {
		this.playbackPoint = -1;
		this.bufferSize = bufferSize;
		this.windowSize = windowSize;
		this.buffer = new TreeSet<Long>();
	}

//-------------------------------------------------------------------	
	public void addBlock(long blockIndex) {
		this.buffer.add(blockIndex);

		if (this.buffer.size() > this.bufferSize)
			this.buffer.pollFirst();
	}

//-------------------------------------------------------------------	
	public boolean contains(long blockIndex) {
		return this.buffer.contains(blockIndex);		
	}

//-------------------------------------------------------------------	
	public long head() {
		long head = -1;
		
		if (this.buffer.size() > 0)
			head = this.buffer.last();
		
		return head;
	}

//-------------------------------------------------------------------	
	public long tail() {
		long tail = -1;
		
		if (this.buffer.size() > 0)
			tail = this.buffer.first();
		
		return tail;
	}

//-------------------------------------------------------------------	
	public ArrayList<Long> getBM() {
		long block;
		ArrayList<Long> BM = new ArrayList<Long>();

		Iterator<Long> iter = this.buffer.descendingIterator();
		
		while (iter.hasNext()) {
			block = iter.next();
			BM.add(block);
			if (BM.size() == this.windowSize)
				break;
		}
		
		return BM;
	}

//-------------------------------------------------------------------	
	public boolean playNext() {
		long oldPlaybackPoint = this.playbackPoint;
		
		if (this.playbackPoint == -1)
			this.playbackPoint = this.buffer.first();
		else if (this.buffer.tailSet(this.playbackPoint + 1).size() > 0)
			this.playbackPoint = this.buffer.tailSet(this.playbackPoint + 1).first();
				
		if (this.playbackPoint - oldPlaybackPoint == 1 || oldPlaybackPoint == -1)
			return true;
		
		return false;
	}

//-------------------------------------------------------------------	
	public long getPlaybackPoint() {
		return this.playbackPoint;
	}

//-------------------------------------------------------------------	
	public String toString() {
		return this.getBM().toString() + " - playback point: " + this.playbackPoint;
	}

//-------------------------------------------------------------------	
	public TreeSet<Long> getBuffer() {
		return this.buffer;
	}

//-------------------------------------------------------------------	
	public TreeSet<Long> getMissedBlocks() {
		TreeSet<Long> missedBlocks = new TreeSet<Long>();
		
		if (this.playbackPoint > 0) {		
			for (long block = this.playbackPoint + Configuration.BUFFER_HELP_NEEDED_SIZE; block < this.head(); block++) {
				if (!this.buffer.contains(block))
					missedBlocks.add(block);
			}
		} else {
			for (long block = this.tail(); block < this.head(); block++) {
				if (!this.buffer.contains(block))
					missedBlocks.add(block);
			}
		}
		
		return missedBlocks;
	}

//-------------------------------------------------------------------	
	public TreeSet<Long> getHelpNeededMissedBlocks() {
		TreeSet<Long> missedBlocks = new TreeSet<Long>();
		
		if (this.playbackPoint > 0) {		
			for (long block = this.playbackPoint; block < this.playbackPoint + Configuration.BUFFER_HELP_NEEDED_SIZE; block++) {
				if (!this.buffer.contains(block))
					missedBlocks.add(block);
			}
		}
	
		return missedBlocks;
	}

}
