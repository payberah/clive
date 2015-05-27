package clive.peer.common;


public class BlockData extends MSMessage {

	private static final long serialVersionUID = -6815596147580962155L;

	private final long blockIndex;
	private final int blockSize;

//-------------------------------------------------------------------	
	public BlockData(MSPeerAddress source, MSPeerAddress destination, long blockIndex, int blockSize) {
		super(source, destination, MSComponents.Partnership);
		this.blockIndex = blockIndex;
		this.blockSize = blockSize;
	}

//-------------------------------------------------------------------	
	public long getBlockIndex() {
		return this.blockIndex;
	}
	
//-------------------------------------------------------------------	
	public int getSize() {
		return this.blockSize;
	}
}
