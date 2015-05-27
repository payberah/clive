package clive.peer.partnership;

import clive.peer.common.MSPeerAddress;

public class BlockHolder implements Comparable<BlockHolder> {

	private final MSPeerAddress address;
	private final long load;

//-------------------------------------------------------------------	
	public BlockHolder(MSPeerAddress address, long load) {
		this.address = address;
		this.load = load;
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getAddress() {
		return this.address;
	}

//-------------------------------------------------------------------	
	public long getLoad() {
		return this.load;
	}

//-------------------------------------------------------------------	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		return result;
	}

//-------------------------------------------------------------------	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockHolder other = (BlockHolder) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		return true;
	}

//-------------------------------------------------------------------	
	@Override
	public int compareTo(BlockHolder obj) {
		if (this.load > obj.load)
			return 1;
		
		return -1;
	}

//-------------------------------------------------------------------	
	@Override
	public String toString() {
		return "[" + address + ", " + load + "]";
	}
}
