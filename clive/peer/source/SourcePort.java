package clive.peer.source;

import clive.peer.source.AddHelper;
import clive.simulator.core.DontNeedHelper;
import clive.simulator.core.NeedHelper;
import se.sics.kompics.PortType;

public class SourcePort extends PortType {{
	negative(JoinSource.class);
	negative(AddHelper.class);
	
	positive(NeedHelper.class);
	positive(DontNeedHelper.class);
}}
