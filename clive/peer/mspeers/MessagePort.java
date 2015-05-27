package clive.peer.mspeers;

import clive.peer.common.MSMessage;
import se.sics.kompics.PortType;

public class MessagePort extends PortType {{
	negative(MSMessage.class);
	positive(MSMessage.class);
}}
