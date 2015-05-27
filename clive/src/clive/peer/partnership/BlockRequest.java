package clive.peer.partnership;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;

public class BlockRequest extends MSMessage {

	private static final long serialVersionUID = -6815596147580962155L;
	private final long blockIndex;

//-------------------------------------------------------------------	
	public BlockRequest(MSPeerAddress source, MSPeerAddress destination, long blockIndex) {
		super(source, destination, MSComponents.Partnership);
		this.blockIndex = blockIndex;
	}

//-------------------------------------------------------------------	
	public long getBlockIndex() {
		return this.blockIndex;
	}

//-------------------------------------------------------------------	
	public int getSize() {
		return 0;
	}
}
