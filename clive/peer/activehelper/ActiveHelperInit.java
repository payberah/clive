package clive.peer.activehelper;

import clive.peer.common.MSPeerAddress;
import clive.peer.membership.GradientConfiguration;
import clive.peer.mspeers.MSConfiguration;

import se.sics.kompics.Init;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;

public final class ActiveHelperInit extends Init {

	private final MSPeerAddress msPeerSelf;
	private final MSPeerAddress mediaSource;
	private final int uploadSlots;
	private final BootstrapConfiguration bootstrapConfiguration;
	private final GradientConfiguration gradientConfiguration;
	private final MSConfiguration msConfiguration;
	private final PingFailureDetectorConfiguration fdConfiguration;

//-------------------------------------------------------------------	
	public ActiveHelperInit(MSPeerAddress msPeerSelf, MSPeerAddress mediaSource, int uploadSlots,
			MSConfiguration msConfiguration,
			BootstrapConfiguration bootstrapConfiguration,
			GradientConfiguration gradientConfiguration,
			PingFailureDetectorConfiguration fdConfiguration) {
		super();
		this.mediaSource = mediaSource;
		this.uploadSlots = uploadSlots;
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
	public MSPeerAddress getSourceAddress() {
		return mediaSource;
	}

//-------------------------------------------------------------------	
	public int getUploadSlots() {
		return this.uploadSlots;
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
