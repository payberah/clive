package clive.simulator.core;

import se.sics.kompics.PortType;
import se.sics.kompics.p2p.experiment.dsl.events.TerminateExperiment;

public class MSSimulatorPort extends PortType {{
	positive(SourceJoin.class);
	positive(MSPeerJoin.class);
	positive(MSPeerFail.class);
	positive(MSZeroJoin.class);
	positive(PassiveHelperJoin.class);
	positive(MSPeerGetPeer.class);
	negative(TerminateExperiment.class);
}}
