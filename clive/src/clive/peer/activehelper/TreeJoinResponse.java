package clive.peer.activehelper;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;
import clive.peer.partnership.RegisterResponse;

public class TreeJoinResponse extends MSMessage {

	private static final long serialVersionUID = -6815596147580962155L;
	private final MSPeerAddress helper;
	private final RegisterResponse result;

//-------------------------------------------------------------------	
	public TreeJoinResponse(MSPeerAddress source, MSPeerAddress destination, MSPeerAddress helper, RegisterResponse result) {
		super(source, destination, MSComponents.Partnership);
		this.helper = helper;
		this.result = result;
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getHelper() {
		return this.helper;
	}
	
//-------------------------------------------------------------------	
	public RegisterResponse getResult() {
		return this.result;
	}

//-------------------------------------------------------------------	
	public int getSize() {
		return 0;
	}
}
