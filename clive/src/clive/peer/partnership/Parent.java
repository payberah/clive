package clive.peer.partnership;


import java.util.ArrayList;
import java.util.TreeSet;

import clive.peer.common.MSPeerAddress;

public class Parent {

	private MSPeerAddress address;
	private TreeSet<Long> BM;
	private final int money;
	private long load;
	private int score = 0;
	
//-------------------------------------------------------------------	
	public Parent(MSPeerAddress address, int money, long load, ArrayList<Long> BM) {
		this.address = address;
		this.BM = new TreeSet<Long>(BM);
		this.money = money;
		this.load = load;
	}

//-------------------------------------------------------------------	
	public void update(long load, ArrayList<Long> BM) {
		this.BM = new TreeSet<Long>(BM);
		this.load = load;
	}

//-------------------------------------------------------------------	
	public void incScore() {
		this.score++;
	}


//-------------------------------------------------------------------	
	public int getScore() {
		return this.score;
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getAddress() {
		return this.address;
	}

//-------------------------------------------------------------------	
	public int getMoney() {
		return this.money;
	}

//-------------------------------------------------------------------	
	public long getLoad() {
		return this.load;
	}

//-------------------------------------------------------------------	
	public TreeSet<Long> getBM() {
		return this.BM;
	}
}
