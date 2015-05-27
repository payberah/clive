package clive.peer.source;


import java.util.Vector;

import clive.peer.common.MSPeerAddress;
import clive.peer.membership.GradientConfiguration;
import clive.peer.mspeers.MSConfiguration;

import se.sics.kompics.Init;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;

public final class SourceInit extends Init {

	private final MSPeerAddress msPeerSelf;
	private final int uploadSlots;
	private final int bw;
	private Vector<MSPeerAddress> freeriders;
	private final BootstrapConfiguration bootstrapConfiguration;
	private final GradientConfiguration gradientConfiguration;
	private final MSConfiguration msConfiguration;
	private final PingFailureDetectorConfiguration fdConfiguration;

//-------------------------------------------------------------------	
	public SourceInit(MSPeerAddress msPeerSelf, int uploadSlots, int bw, Vector<MSPeerAddress> freeriders,
			MSConfiguration msConfiguration,
			BootstrapConfiguration bootstrapConfiguration,
			GradientConfiguration gradientConfiguration,
			PingFailureDetectorConfiguration fdConfiguration) {
		super();
		this.uploadSlots = uploadSlots;
		this.bw = bw;
		this.freeriders = freeriders;
		this.msPeerSelf = msPeerSelf;
		this.bootstrapConfiguration = bootstrapConfiguration;
		this.gradientConfiguration = gradientConfiguration;
		this.msConfiguration = msConfiguration;
		this.fdConfiguration = fdConfiguration;
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
	public int getBW() {
		return this.bw;
	}

//-------------------------------------------------------------------	
	public Vector<MSPeerAddress> getFreeriders() {
		return this.freeriders;
	}
	
//-------------------------------------------------------------------	
	public BootstrapConfiguration getBootstrapConfiguration() {
		return bootstrapConfiguration;
	}

//-------------------------------------------------------------------	
	public GradientConfiguration getGradientConfiguration() {
		return gradientConfiguration;
	}

//-------------------------------------------------------------------	
	public MSConfiguration getMSConfiguration() {
		return msConfiguration; 
	}
	
//-------------------------------------------------------------------	
	public PingFailureDetectorConfiguration getFdConfiguration() {
		return fdConfiguration;
	}
}
