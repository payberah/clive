package clive.simulator.core;

import clive.peer.membership.GradientConfiguration;
import clive.peer.mspeers.MSConfiguration;
import se.sics.kompics.Init;
import se.sics.kompics.address.Address;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;

public final class MSSimulatorInit extends Init {

	private final MSConfiguration msConfiguration;
	private final BootstrapConfiguration bootstrapConfiguration;
	private final GradientConfiguration gradientConfiguration;
	private final PingFailureDetectorConfiguration fdConfiguration;
	
	private final Address peer0Address;

//-------------------------------------------------------------------	
	public MSSimulatorInit(MSConfiguration msConfiguration,
			BootstrapConfiguration bootstrapConfiguration,
			GradientConfiguration gradientConfiguration, 
			PingFailureDetectorConfiguration fdConfiguration,
			Address peer0Address) {
		super();
		this.msConfiguration = msConfiguration;
		this.bootstrapConfiguration = bootstrapConfiguration;
		this.gradientConfiguration = gradientConfiguration;
		this.fdConfiguration = fdConfiguration;
		this.peer0Address = peer0Address;
	}

//-------------------------------------------------------------------	
	public MSConfiguration getMSConfiguration() {
		return msConfiguration;
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
	public PingFailureDetectorConfiguration getFdConfiguration() {
		return fdConfiguration;
	}

//-------------------------------------------------------------------	
	public Address getPeer0Address() {
		return peer0Address;
	}
}
