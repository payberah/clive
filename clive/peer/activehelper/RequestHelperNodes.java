package clive.peer.activehelper;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;

public class RequestHelperNodes extends MSMessage {

	private static final long serialVersionUID = -6815596147580962155L;

//-------------------------------------------------------------------	
	public RequestHelperNodes(MSPeerAddress source, MSPeerAddress destination) {
		super(source, destination, MSComponents.Partnership);
	}

//-------------------------------------------------------------------	
	public int getSize() {
		return 0;
	}
}
