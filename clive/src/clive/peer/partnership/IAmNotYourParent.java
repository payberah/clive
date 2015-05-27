package clive.peer.partnership;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;

public class IAmNotYourParent extends MSMessage {

	private static final long serialVersionUID = -9176558774040739598L;

//-------------------------------------------------------------------	
	public IAmNotYourParent(MSPeerAddress source, MSPeerAddress destination) {
		super(source, destination, MSComponents.Partnership);
	}

//-------------------------------------------------------------------	
	public int getSize() {
		return 0;
	}
}
