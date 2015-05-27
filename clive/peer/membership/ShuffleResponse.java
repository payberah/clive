package clive.peer.membership;

import java.util.UUID;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;

public class ShuffleResponse extends MSMessage {

	private static final long serialVersionUID = -5022051054665787770L;
	private final UUID requestId;
	private final DescriptorBuffer randomBuffer;
	private double[] hitRatio;
	private double netSize;
	private int timestamp;

//-------------------------------------------------------------------
	public ShuffleResponse(MSPeerAddress source, MSPeerAddress destination, UUID requestId, DescriptorBuffer randomBuffer, double[] hitRatio, double netSize, int timestamp) {
		super(source, destination, MSComponents.Membership);
		this.requestId = requestId;
		this.randomBuffer = randomBuffer;
		this.hitRatio = hitRatio;
		this.netSize = netSize;
		this.timestamp = timestamp;
	}

//-------------------------------------------------------------------
	public UUID getRequestId() {
		return requestId;
	}

//-------------------------------------------------------------------
	public DescriptorBuffer getRandomBuffer() {
		return randomBuffer;
	}

//-------------------------------------------------------------------
	public double[] getHitRatio() {
		return hitRatio;
	}

//-------------------------------------------------------------------
	public double getNetSize() {
		return netSize;
	}

//-------------------------------------------------------------------
	public int getTimestamp() {
		return timestamp;
	}

//-------------------------------------------------------------------
	public int getSize() {
		return 0;
	}
}
