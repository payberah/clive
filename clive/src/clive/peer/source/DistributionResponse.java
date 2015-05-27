package clive.peer.source;

import clive.peer.common.MSComponents;
import clive.peer.common.MSMessage;
import clive.peer.common.MSPeerAddress;


public class DistributionResponse extends MSMessage {

	private static final long serialVersionUID = 8493601671018888143L;
	private double[] distribution;
	private double netSize;

//-------------------------------------------------------------------
	public DistributionResponse(MSPeerAddress source, MSPeerAddress destination, double[] distribution, double netSize) {
		super(source, destination, MSComponents.Membership);
		this.distribution = distribution;
		this.netSize = netSize;
	}

//-------------------------------------------------------------------
	public double[] getDistribution() {
		return this.distribution;
	}

//-------------------------------------------------------------------
	public double getNetSize() {
		return this.netSize;
	}

//-------------------------------------------------------------------
	public int getSize() {
		return 0;
	}
}
