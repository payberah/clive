package clive.peer.partnership;


import java.util.ArrayList;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;

public class MarketMsgRequest extends MSMessage {

	private static final long serialVersionUID = -6815596147580962155L;
	private final int money;
	private final long playbackPoint;
	private final RegisterRequest request;
	private final ArrayList<Long> BM;
	private final ArrayList<MSPeerAddress> children;

//-------------------------------------------------------------------	
	public MarketMsgRequest(MSPeerAddress source, MSPeerAddress destination, int money, long playbackPoint, ArrayList<Long> BM, ArrayList<MSPeerAddress> children, RegisterRequest request) {
		super(source, destination, MSComponents.Partnership);
		this.money = money;
		this.playbackPoint = playbackPoint;
		this.BM = BM;
		this.children = children;
		this.request = request;
	}

//-------------------------------------------------------------------	
	public MarketMsgRequest(MSPeerAddress source, MSPeerAddress destination, RegisterRequest request) {
		super(source, destination, MSComponents.Partnership);
		this.money = 0;
		this.playbackPoint = 0;
		this.BM = null;
		this.children = null;
		this.request = request;
	}

//-------------------------------------------------------------------	
	public int getMoney() {
		return this.money;
	}

//-------------------------------------------------------------------	
	public long getPlaybackPoint() {
		return this.playbackPoint;
	}

//-------------------------------------------------------------------	
	public ArrayList<Long> getBM() {
		return this.BM;
	}

//-------------------------------------------------------------------	
	public ArrayList<MSPeerAddress> getChildren() {
		return this.children;
	}

//-------------------------------------------------------------------	
	public RegisterRequest getRequest() {
		return this.request;
	}

//-------------------------------------------------------------------	
	public int getSize() {
		return 0;
	}
}
