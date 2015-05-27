package clive.peer.partnership;

import clive.peer.common.Buffer;
import clive.peer.common.MSPeerAddress;
import clive.peer.common.RandomView;
import clive.peer.mspeers.MSConfiguration;

import se.sics.kompics.Init;

public final class PartnershipInit extends Init {

	private final MSPeerAddress self;
	private RandomView randomView;
	private int uploadSlots;
	private Buffer buffer;
	private final long sendBMPeriod;
	private final long requestBlockPeriod;	
	private final long updateParentsPeriod;
	private final int retryPeriod;
	private final int partnersViewSize;
	private final int streamRate;
	private final int blockSize;
	private final int bw;
	private final int bufferingTime;

	private final MSPeerAddress helperAddress;
	private final MSPeerAddress sourceAddress;
	private MSPeerAddress[] helperChildren;

//-------------------------------------------------------------------	
	public PartnershipInit(MSPeerAddress self, RandomView randomView, int uploadSlots, Buffer buffer, MSConfiguration msConfiguration, int bw, MSPeerAddress helperAddress, MSPeerAddress sourceAddress, MSPeerAddress[] helperChildren) {
		super();
		this.self = self;
		this.randomView = randomView;
		this.uploadSlots = uploadSlots;
		this.buffer = buffer;
		this.sendBMPeriod = msConfiguration.getSendBMPeriod();
		this.requestBlockPeriod = msConfiguration.getRequestBlockPeriod();
		this.updateParentsPeriod = msConfiguration.getUpdateParentsPeriod();
		this.retryPeriod = msConfiguration.getJoinTimeout();
		this.partnersViewSize = msConfiguration.getPartnerViewSize();
		this.streamRate = msConfiguration.getStreamRate();
		this.blockSize = msConfiguration.getBlockSize();
		this.bufferingTime = msConfiguration.getBufferTime();
		this.bw = bw;
		this.helperAddress = helperAddress;
		this.sourceAddress = sourceAddress;
		this.helperChildren = helperChildren;
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getSelf() {
		return this.self;
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getHelperAddress() {
		return this.helperAddress;
	}

//-------------------------------------------------------------------	
	public MSPeerAddress getSourceAddress() {
		return this.sourceAddress;
	}

//-------------------------------------------------------------------	
	public RandomView getRandomView() {
		return this.randomView;
	}

//-------------------------------------------------------------------	
	public int getUploadSlots() {
		return this.uploadSlots;
	}

//-------------------------------------------------------------------	
	public Buffer getBuffer() {
		return this.buffer;
	}

//-------------------------------------------------------------------	
	public long getSendBMPeriod() {
		return this.sendBMPeriod;
	}

//-------------------------------------------------------------------	
	public long getRequestBlockPeriod() {
		return this.requestBlockPeriod;
	}

//-------------------------------------------------------------------	
	public long getUpdateParentsPeriod() {
		return this.updateParentsPeriod;
	}

//-------------------------------------------------------------------	
	public int getRetryPeriod() {
		return this.retryPeriod;
	}

//-------------------------------------------------------------------	
	public int getPartnersViewSize() {
		return this.partnersViewSize;
	}

//-------------------------------------------------------------------	
	public int getStreamRate() {
		return this.streamRate;
	}

//-------------------------------------------------------------------	
	public int getBufferingTime() {
		return this.bufferingTime;
	}
	
//-------------------------------------------------------------------	
	public int getBlockSize() {
		return this.blockSize;
	}

//-------------------------------------------------------------------	
	public int getBW() {
		return this.bw;
	}
	
//-------------------------------------------------------------------	
	public MSPeerAddress[] getHelperChildren() {
		return this.helperChildren;
	}
}