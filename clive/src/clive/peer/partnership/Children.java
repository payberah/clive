package clive.peer.partnership;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import clive.peer.common.MSPeerAddress;

public class Children {
	private int freeSlots;
	private HashMap<MSPeerAddress, Child> children = new HashMap<MSPeerAddress, Child>();

//-------------------------------------------------------------------	
	public Children(int capacity) {
		this.freeSlots = capacity;
	}

//-------------------------------------------------------------------	
	public void add(MSPeerAddress peer, int money, ArrayList<MSPeerAddress> grandChildren) {
		if (this.freeSlots > 0) {
			this.freeSlots--;
			this.children.put(peer, new Child(peer, money, grandChildren));
		}
	}

//-------------------------------------------------------------------	
	public void remove(MSPeerAddress peer) {
		if (this.children.containsKey(peer)) {
			this.freeSlots++;
			this.children.remove(peer);
		}
	}

//-------------------------------------------------------------------	
	public boolean contains(MSPeerAddress peer) {
		return this.children.containsKey(peer);
	}

//-------------------------------------------------------------------	
	public boolean hasFreeSlots() {
		return this.freeSlots > 0 ? true : false;
	}

//-------------------------------------------------------------------	
	public int getFreeSlots() {
		return this.freeSlots;
	}

//-------------------------------------------------------------------	
	public ArrayList<MSPeerAddress> getChildren() {
		return new ArrayList<MSPeerAddress>(this.children.keySet());
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getCheapest(int peerMoney) {
		MSPeerAddress cheapest = null;
		TreeSet<Child> sortedChildren = new TreeSet<Child>(this.children.values());
		
		for (Child child : sortedChildren) {
			if (child.getScore() <= 0) {
				cheapest = child.getAddress();
				break;
			} else if (child.getMoney() < peerMoney) {
				cheapest = child.getAddress();
				break;
			}
		}
		
		
		//System.out.println(sortedChildren + ", cheapest: " + cheapest);

		return cheapest;
	}

//-------------------------------------------------------------------	
//	public MSPeerAddress getCheapest(int peerMoney) {
//		MSPeerAddress cheapest = null;
//		int minMoney = peerMoney;
//		int money;
//		
//		for (MSPeerAddress peer : children.keySet()) {
//			money = this.children.get(peer).getMoney();
//			if (money <= minMoney) {
//				minMoney = money;
//				cheapest = peer;
//			}
//		}
//		
//		return cheapest;
//	}

//-------------------------------------------------------------------	
	public void updateGrandChildren(MSPeerAddress peer, ArrayList<MSPeerAddress> grandChildren) {
		Child child = this.children.get(peer);
		
		if (child != null)
			child.updateGrandChildren(grandChildren);
	}

//-------------------------------------------------------------------	
	public void updateScore(MSPeerAddress peer, int score) {
		Child child = this.children.get(peer);
		
		if (child != null)
			child.updateScore(score);
	}

//-------------------------------------------------------------------	
	public ArrayList<MSPeerAddress> getGrandChildren(MSPeerAddress peer) {
		ArrayList<MSPeerAddress> grandChildren = null;
		
		Child child = this.children.get(peer);
		
		if (child != null)
			grandChildren = child.getGrandChildren();
		
		return grandChildren;
	}

//-------------------------------------------------------------------	
	@Override
	public String toString() {
		return children.toString();
	}
	
	
}
