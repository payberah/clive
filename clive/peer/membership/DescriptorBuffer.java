package clive.peer.membership;


import java.io.Serializable;
import java.util.ArrayList;

import clive.peer.common.MSPeerAddress;
import clive.peer.common.MSPeerDescriptor;


public class DescriptorBuffer implements Serializable {

	private static final long serialVersionUID = -4414783055393007206L;
	private final MSPeerAddress from;
	private final ArrayList<MSPeerDescriptor> descriptors;

//-------------------------------------------------------------------
	public DescriptorBuffer(MSPeerAddress from, ArrayList<MSPeerDescriptor> descriptors) {
		super();
		this.from = from;
		this.descriptors = descriptors;
	}

//-------------------------------------------------------------------
	public MSPeerAddress getFrom() {
		return from;
	}

//-------------------------------------------------------------------
	public int getSize() {
		return descriptors.size();
	}

//-------------------------------------------------------------------
	public ArrayList<MSPeerDescriptor> getDescriptors() {
		return descriptors;
	}
}
