package clive.peer.activehelper;

import clive.peer.source.AddHelper;
import clive.simulator.core.MSPeerFail;
import clive.simulator.core.MSPeerJoin;
import se.sics.kompics.PortType;

public class ActiveHelperPort extends PortType {{
	negative(JoinActiveHelper.class);
	negative(AddHelper.class);
	
	positive(MSPeerJoin.class);
	positive(MSPeerFail.class);
}}
