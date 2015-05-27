package clive.peer.activehelper;

import java.util.ArrayList;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;

public class RegisterHelperNodes extends MSMessage {

	private static final long serialVersionUID = -6815596147580962155L;
	private final ArrayList<MSPeerAddress> nodes;

//-------------------------------------------------------------------	
	public RegisterHelperNodes(MSPeerAddress source, MSPeerAddress destination, ArrayList<MSPeerAddress> nodes) {
		super(source, destination, MSComponents.Partnership);
		this.nodes = nodes;
	}

//-------------------------------------------------------------------	
	public int getSize() {
		return 0;
	}
	
//-------------------------------------------------------------------	
	public ArrayList<MSPeerAddress> getNodes() {
		return this.nodes;
	}
}
