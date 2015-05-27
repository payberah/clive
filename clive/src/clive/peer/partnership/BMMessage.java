package clive.peer.partnership;


import java.util.ArrayList;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;

public class BMMessage extends MSMessage {

	private static final long serialVersionUID = -6815596147580962155L;
	private final ArrayList<Long> BM;
	private final long load;

//-------------------------------------------------------------------	
	public BMMessage(MSPeerAddress source, MSPeerAddress destination, long load, ArrayList<Long> BM) {
		super(source, destination, MSComponents.Partnership);
		this.BM = BM;
		this.load = load;
	}

//-------------------------------------------------------------------	
	public ArrayList<Long> getBM() {
		return this.BM;
	}

//-------------------------------------------------------------------	
	public long getLoad() {
		return this.load;
	}

//-------------------------------------------------------------------	
	public int getSize() {
		return 0;
	}
}
