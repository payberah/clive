package clive.peer.mspeers;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public final class MSConfiguration {

	private final int bandwidthUnit;
	private final int streamRate;
	private final int blockSize;
	private final long sendBMPeriod;
	private final long requestBlockPeriod;
	private final long updateParentsPeriod;
	private final int bufferTime;
	private final int joinTimeout;
	private final int bufferSize;
	private final int partnersViewSize;
	private final int snapshotPeriod;

//-------------------------------------------------------------------	
	public MSConfiguration(int bandwidthUnit, int streamRate, int blockSize, long sendBMPeriod, long requestBlockPeriod, long updateParentsPeriod, int bufferTime, int joinTimeout, int bufferSize, int partnerViewSize, int snapshotPeriod) {
		super();
		this.bandwidthUnit = bandwidthUnit;
		this.streamRate = streamRate;
		this.blockSize = blockSize;
		this.sendBMPeriod = sendBMPeriod;
		this.requestBlockPeriod = requestBlockPeriod;
		this.updateParentsPeriod = updateParentsPeriod;
		this.bufferTime = bufferTime;
		this.joinTimeout = joinTimeout;
		this.bufferSize = bufferSize;
		this.partnersViewSize = partnerViewSize;
		this.snapshotPeriod = snapshotPeriod;
	}

//-------------------------------------------------------------------	
	public int getBandwidthUnit() {
		return this.bandwidthUnit;
	}

//-------------------------------------------------------------------	
	public int getStreamRate() {
		return this.streamRate;
	}

//-------------------------------------------------------------------	
	public int getBlockSize() {
		return this.blockSize;
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
	public int getBufferTime() {
		return this.bufferTime;
	}

//-------------------------------------------------------------------	
	public int getJoinTimeout() {
		return this.joinTimeout;
	}

//-------------------------------------------------------------------	
	public int getBufferSize() {
		return this.bufferSize;
	}

//-------------------------------------------------------------------	
	public int getPartnerViewSize() {
		return this.partnersViewSize;
	}

//-------------------------------------------------------------------	
	public int getSnapshotPeriod() {
		return this.snapshotPeriod;
	}

//-------------------------------------------------------------------	
	public void store(String file) throws IOException {
		Properties p = new Properties();
		p.setProperty("bandwidth", "" + this.bandwidthUnit);
		p.setProperty("stream.rate", "" + this.streamRate);
		p.setProperty("block.size", "" + this.blockSize);
		p.setProperty("send.bm.period", "" + this.sendBMPeriod);
		p.setProperty("request.block.period", "" + this.requestBlockPeriod);
		p.setProperty("update.parents.period", "" + this.updateParentsPeriod);
		p.setProperty("buffer.time", "" + this.bufferTime);
		p.setProperty("join.timeout", "" + this.joinTimeout);
		p.setProperty("buffer.size", "" + this.bufferSize);
		p.setProperty("partner.view.size", "" + this.partnersViewSize);
		p.setProperty("snapshot.period", "" + this.snapshotPeriod);
		
		Writer writer = new FileWriter(file);
		p.store(writer, "se.sics.kompics.p2p.ms");
	}

//-------------------------------------------------------------------	
	public static MSConfiguration load(String file) throws IOException {
		Properties p = new Properties();
		Reader reader = new FileReader(file);
		p.load(reader);

		int bandwidth = Integer.parseInt(p.getProperty("bandwidth"));
		int streamRate = Integer.parseInt(p.getProperty("stream.rate"));
		int blockSize = Integer.parseInt(p.getProperty("block.size"));
		long sendBMPeriod = Long.parseLong(p.getProperty("send.bm.period"));
		long requestBlockPeriod = Long.parseLong(p.getProperty("request.block.period"));
		long updateParentsPeriod = Long.parseLong(p.getProperty("update.parents.period"));
		int bufferTime = Integer.parseInt(p.getProperty("buffer.time"));
		int joinTimeout = Integer.parseInt(p.getProperty("join.timeout"));
		int bufferSize = Integer.parseInt(p.getProperty("buffer.size"));
		int partnerViewSize = Integer.parseInt(p.getProperty("partner.view.size"));
		int snapshotPeriod = Integer.parseInt(p.getProperty("snapshot.period"));
		
		return new MSConfiguration(bandwidth, streamRate, blockSize, sendBMPeriod, requestBlockPeriod, updateParentsPeriod, bufferTime, joinTimeout, bufferSize, partnerViewSize, snapshotPeriod);
	}
}
