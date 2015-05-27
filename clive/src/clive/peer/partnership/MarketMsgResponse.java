package clive.peer.partnership;

import java.util.ArrayList;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;


public class MarketMsgResponse extends MSMessage {

	private static final long serialVersionUID = -6815596147580962155L;

	private final RegisterResponse response;
	private final int money;
	private final long load;
	private final ArrayList<Long> BM;

//-------------------------------------------------------------------	
	public MarketMsgResponse(MSPeerAddress source, MSPeerAddress destination, int money, long load, ArrayList<Long> BM, RegisterResponse response) {
		super(source, destination, MSComponents.Partnership);
		this.response = response;
		this.money = money;
		this.load = load;
		this.BM = BM;
	}

//-------------------------------------------------------------------	
	public RegisterResponse getResponse() {
		return this.response;
	}

//-------------------------------------------------------------------	
	public int getMoney() {
		return this.money;
	}

//-------------------------------------------------------------------	
	public long getLoad() {
		return this.load;
	}

//-------------------------------------------------------------------	
	public ArrayList<Long> getBM() {
		return this.BM;
	}

//-------------------------------------------------------------------	
	public int getSize() {
		return 0;
	}
}
