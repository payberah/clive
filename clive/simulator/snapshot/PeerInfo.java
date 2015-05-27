package clive.simulator.snapshot;


import java.util.ArrayList;

import clive.peer.common.Buffer;
import clive.peer.common.MSPeerAddress;

public class PeerInfo {
	private Buffer buffer;
	private long birthTime;
	private long totalPlayedBlocks;
	private long missedBlocks;
	private long timeToJoin;
	private int bw;
	private int load;
	private ArrayList<MSPeerAddress> parents;
	private ArrayList<MSPeerAddress> children;
	private int timestamp;
	private double[] ratio;

//-------------------------------------------------------------------
	public PeerInfo() {
	}

//-------------------------------------------------------------------
	public PeerInfo(Buffer buffer, int bw, long birthTime) {
		this.buffer = buffer;
		this.birthTime = birthTime;
		this.totalPlayedBlocks = 0;
		this.missedBlocks = 0;
		this.bw = bw;
	}

//-------------------------------------------------------------------
	public void update(Buffer buffer) {
		this.buffer = buffer;
	}

//-------------------------------------------------------------------
	public void updateParents(ArrayList<MSPeerAddress> parents) {
		this.parents = parents;
	}

//-------------------------------------------------------------------
	public void updateChildren(ArrayList<MSPeerAddress> children) {
		this.children = children;
	}
	
//-------------------------------------------------------------------
	public void updateLoad() {
		this.load++;
	}

//-------------------------------------------------------------------
	public void updateRatio(double[] ratio) {
		this.ratio = ratio;
	}
	
//-------------------------------------------------------------------
	public int getTimestamp() {
		return this.timestamp;
	}

//-------------------------------------------------------------------
	public void incPlayedBlocks() {
		this.totalPlayedBlocks++;
	}

//-------------------------------------------------------------------
	public void incMissedBlocks() {
		this.missedBlocks++;
	}

//-------------------------------------------------------------------
	public void setJoinTime(long joinTime) {
		this.timeToJoin = joinTime - this.birthTime;
	}

//-------------------------------------------------------------------
	public Buffer getBuffer() {
		return this.buffer;
	}

//-------------------------------------------------------------------
	public int getBW() {
		return this.bw;
	}

//-------------------------------------------------------------------
	public ArrayList<MSPeerAddress> getParents() {
		return this.parents;
	}

//-------------------------------------------------------------------
	public ArrayList<MSPeerAddress> getChildren() {
		return this.children;
	}
	
//-------------------------------------------------------------------
	public long getBirthTime() {
		return this.birthTime;
	}

//-------------------------------------------------------------------
	public long getTotalPlayedBlocks() {
		return this.totalPlayedBlocks;
	}

//-------------------------------------------------------------------
	public long getMissedBlocks() {
		return this.missedBlocks;
	}

//-------------------------------------------------------------------
	public long getTimeToJoin() {
		return this.timeToJoin;
	}
	
//-------------------------------------------------------------------
	public int getLoad() {
		return this.load;
	}
	
//-------------------------------------------------------------------
	public double[] getRatio() {
		return this.ratio;
	}
}
