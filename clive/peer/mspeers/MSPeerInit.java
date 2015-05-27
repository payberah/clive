package clive.peer.mspeers;


import java.util.Vector;

import clive.peer.common.MSPeerAddress;
import clive.peer.membership.GradientConfiguration;

import se.sics.kompics.Init;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;

public final class MSPeerInit extends Init {

	private final MSPeerAddress msPeerSelf;
	private final int uploadSlots;
	private final int downloadSlots;
	private final int bw;
	private Vector<MSPeerAddress> freeriders;
	private final BootstrapConfiguration bootstrapConfiguration;
	private final GradientConfiguration gradientConfiguration;
	private final MSConfiguration msConfiguration;
	private final PingFailureDetectorConfiguration fdConfiguration;
	private final MSPeerAddress helperAddress;
	private final MSPeerAddress sourceAddress;

//-------------------------------------------------------------------	
	public MSPeerInit(MSPeerAddress msPeerSelf, int uploadSlots, int downloadSlots, int bw, Vector<MSPeerAddress> freeriders,
			MSConfiguration msConfiguration,
			BootstrapConfiguration bootstrapConfiguration,
			GradientConfiguration gradientConfiguration,
			PingFailureDetectorConfiguration fdConfiguration,
			MSPeerAddress helperAddress,
			MSPeerAddress sourceAddress) {
		super();
		this.uploadSlots = uploadSlots;
		this.downloadSlots = downloadSlots;
		this.bw = bw;
		this.msPeerSelf = msPeerSelf;
		this.freeriders = freeriders;
		this.bootstrapConfiguration = bootstrapConfiguration;
		this.gradientConfiguration = gradientConfiguration;
		this.msConfiguration = msConfiguration;
		this.fdConfiguration = fdConfiguration;
		this.helperAddress = helperAddress;
		this.sourceAddress = sourceAddress;
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getMSPeerSelf() {
		return msPeerSelf;
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getHelperAddress() {
		return helperAddress;
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getSourceAddress() {
		return sourceAddress;
	}
	
//-------------------------------------------------------------------	
	public int getUploadSlots() {
		return this.uploadSlots;
	}

//-------------------------------------------------------------------	
	public int getDownloadSlots() {
		return this.downloadSlots;
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
