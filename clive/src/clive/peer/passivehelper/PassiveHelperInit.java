package clive.peer.passivehelper;


import clive.peer.common.MSPeerAddress;
import clive.peer.mspeers.MSConfiguration;

import se.sics.kompics.Init;

public final class PassiveHelperInit extends Init {

	private final MSPeerAddress msPeerSelf;
	private final int uploadSlots;
	private final MSConfiguration msConfiguration;

//-------------------------------------------------------------------	
	public PassiveHelperInit(MSPeerAddress msPeerSelf, int uploadSlots, MSConfiguration msConfiguration) {
		super();
		this.uploadSlots = uploadSlots;
		this.msPeerSelf = msPeerSelf;
		this.msConfiguration = msConfiguration;
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getMSPeerSelf() {
		return msPeerSelf;
	}

//-------------------------------------------------------------------	
	public int getUploadSlots() {
		return this.uploadSlots;
	}

//-------------------------------------------------------------------	
	public MSConfiguration getMSConfiguration() {
		return msConfiguration; 
	}
}
