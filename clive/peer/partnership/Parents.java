package clive.peer.partnership;

import java.util.ArrayList;
import java.util.HashMap;

import clive.main.Configuration;
import clive.peer.common.MSPeerAddress;

public class Parents {
	private int freeSlots;
	private long drift;
	private HashMap<MSPeerAddress, Parent> parents = new HashMap<MSPeerAddress, Parent>();

//-------------------------------------------------------------------	
	public Parents(int capacity, long drift) {
		this.freeSlots = capacity;
		this.drift = drift;
	}

//-------------------------------------------------------------------	
	public void add(MSPeerAddress peer, int money, long load, ArrayList<Long> BM) {
		if (this.freeSlots > 0) {
			this.freeSlots--;
			this.parents.put(peer, new Parent(peer, money, load, BM));
		}
	}

//-------------------------------------------------------------------	
	public void remove(MSPeerAddress peer) {
		if (this.parents.containsKey(peer)) {
			this.freeSlots++;
			this.parents.remove(peer);
		}
	}

//-------------------------------------------------------------------	
	public void update(MSPeerAddress peer, long load, ArrayList<Long> BM) {
		Parent parent = this.parents.get(peer);
		
		if (parent != null)
			parent.update(load, BM);
	}

//-------------------------------------------------------------------	
	public void incScore(MSPeerAddress peer) {
		Parent parent = this.parents.get(peer);
		
		if (parent != null)
			parent.incScore();
	}

//-------------------------------------------------------------------	
	public boolean contains(MSPeerAddress peer) {
		return this.parents.containsKey(peer);
	}

//-------------------------------------------------------------------	
	public boolean hasFreeSlots() {
		return this.freeSlots > 0 ? true : false;
	}

//-------------------------------------------------------------------	
	public ArrayList<MSPeerAddress> getParents() {
		return new ArrayList<MSPeerAddress>(this.parents.keySet());
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getCheapestParent() {
		MSPeerAddress cheapest = null;
		int minMoney = Integer.MAX_VALUE;
		int money;
		
		for (MSPeerAddress peer : parents.keySet()) {
			money = this.parents.get(peer).getMoney();
			if (money < minMoney) {
				minMoney = money;
				cheapest = peer;
			}
		}
		
		return cheapest;
	}

//-------------------------------------------------------------------	
	public int getLowestMoney() {
		int minMoney = Integer.MAX_VALUE;
		int money;
		
		for (MSPeerAddress peer : parents.keySet()) {
			money = this.parents.get(peer).getMoney();
			if (money < minMoney) {
				minMoney = money;
			}
		}
		
		if (minMoney == Integer.MAX_VALUE)
			minMoney = -1;
		
		return minMoney;
	}
//-------------------------------------------------------------------	
	public long getPreferedBlockIndex() {
		long blockIndex = -1;
		long biggestBlockIndex = Long.MIN_VALUE;
		long smallestBlockIndex = Long.MAX_VALUE;
		long partnerBlockIndex;
		Parent smallestParent = null;
		
		for (Parent parent : parents.values()) {
			if (parent.getBM().size() == 0)
				continue;
			
			partnerBlockIndex = parent.getBM().last();
			
			if (biggestBlockIndex < partnerBlockIndex)
				biggestBlockIndex = partnerBlockIndex;
			
			if (smallestBlockIndex > partnerBlockIndex) {
				smallestBlockIndex = partnerBlockIndex;
				smallestParent = parent;
			}
		}
		
		if (biggestBlockIndex != Long.MIN_VALUE) {
			//FileIO.append("biggest: " + biggestBlockIndex + ", smallest: " + smallestBlockIndex + "\n", "clive.out");
			if (smallestBlockIndex - drift > smallestParent.getBM().first())
				blockIndex = smallestBlockIndex - drift;
			else
				blockIndex = smallestParent.getBM().first();
		}

//		if (biggestBlockIndex != Long.MIN_VALUE) {
//			FileIO.append("biggest: " + biggestBlockIndex + ", smallest: " + smallestBlockIndex + "\n", "clive.out");
//			if (biggestBlockIndex - smallestBlockIndex > this.drift * 2)
//				blockIndex = biggestBlockIndex - ((biggestBlockIndex - smallestBlockIndex) / 2);
//			else if (biggestBlockIndex - this.drift < getSmallestBlockInPartners())
//				blockIndex = getSmallestBlockInPartners();
//			else
//				blockIndex = biggestBlockIndex - this.drift;
//		}
		
//		if (smallestBlockIndex != Long.MAX_VALUE)
//			blockIndex = smallestBlockIndex;

		return blockIndex;
	}

//-------------------------------------------------------------------	
	public long getSmallestBlockInPartners() {
		long smallestBlockIndex = Long.MAX_VALUE;
		long partnerBlockIndex;
		
		for (Parent parent : parents.values()) {
			if (parent.getBM().size() == 0 || parent.getAddress().getPeerId().equals(Configuration.SOURCE_ID))
				continue;
			
			partnerBlockIndex = parent.getBM().first();
			
			if (smallestBlockIndex > partnerBlockIndex)
				smallestBlockIndex = partnerBlockIndex;
		}
		
		if (smallestBlockIndex == Long.MAX_VALUE)
			smallestBlockIndex = -1;
		
		return smallestBlockIndex;
	}
//-------------------------------------------------------------------	
	public HashMap<Long, ArrayList<BlockHolder>> getBlockHolders(long startIndex, long endIndex) {
		HashMap<Long, ArrayList<BlockHolder>> holders = new HashMap<Long, ArrayList<BlockHolder>>();
		ArrayList<BlockHolder> blockHolders;
		
		for (long index = startIndex; index <= endIndex; index++) {
			blockHolders = new ArrayList<BlockHolder>();
			
			for (Parent parent : parents.values()) {
				if (parent.getBM().contains(index))
					blockHolders.add(new BlockHolder(parent.getAddress(), parent.getLoad()));
			}
			
			holders.put(index, blockHolders);
		}
		
		return holders;
	}

//-------------------------------------------------------------------	
	public ArrayList<BlockHolder> getBlockHolders(long index) {
		ArrayList<BlockHolder> blockHolders = new ArrayList<BlockHolder>();

		for (Parent parent : parents.values()) {
			if (parent.getBM().contains(index))
				blockHolders.add(new BlockHolder(parent.getAddress(), parent.getLoad()));
		}
			
		return blockHolders;
	}

//-------------------------------------------------------------------	
	public HashMap<MSPeerAddress, Integer> getParentScores() {
		HashMap<MSPeerAddress, Integer> scores = new HashMap<MSPeerAddress, Integer>();

		for (MSPeerAddress parent : parents.keySet())
			scores.put(parent, parents.get(parent).getScore());
			
		return scores;
	}

	
//-------------------------------------------------------------------	
	@Override
	public String toString() {
		return this.parents.toString();
	}
}
