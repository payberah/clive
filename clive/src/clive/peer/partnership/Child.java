package clive.peer.partnership;

import java.util.ArrayList;

import clive.peer.common.MSPeerAddress;

public class Child implements Comparable<Child> {

	private MSPeerAddress address;
	private int money;
	private int score;
	private ArrayList<MSPeerAddress> grandChildren;

//-------------------------------------------------------------------	
	public Child(MSPeerAddress address, int money, ArrayList<MSPeerAddress> grandChildren) {
		this.address = address;
		this.money = money;
		this.grandChildren = grandChildren;
		this.score = 0;
	}

//-------------------------------------------------------------------	
	public void updateGrandChildren(ArrayList<MSPeerAddress> grandChildren) {
		this.grandChildren = grandChildren; 
	}

//-------------------------------------------------------------------	
	public void updateScore(int score) {
		this.score += score; 
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
	public int getScore() {
		return this.score;
	}

//-------------------------------------------------------------------	
	public ArrayList<MSPeerAddress> getGrandChildren() {
		return this.grandChildren; 
	}

//-------------------------------------------------------------------	
	@Override
	public int compareTo(Child o) {
		if (this.score > o.score)
			return 1;
		else if (this.score < o.score)
			return -1;
		else if (this.money > o.money)
			return 1;
		else
			return -1;
	}
}
