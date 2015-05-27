package clive.peer.source;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;


public class DistributionRequest extends MSMessage {

	private static final long serialVersionUID = 8493601671018888143L;

//-------------------------------------------------------------------
	public DistributionRequest(MSPeerAddress source, MSPeerAddress destination) {
		super(source, destination, MSComponents.Membership);
	}

//-------------------------------------------------------------------
	public int getSize() {
		return 0;
	}
}
