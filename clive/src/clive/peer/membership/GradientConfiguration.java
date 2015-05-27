package clive.peer.membership;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.util.Properties;

public final class GradientConfiguration {
	private final int shuffleLength;
	private final int randomViewSize;
	private final int similarViewSize;
	private final long shufflePeriod;
	private final long shuffleTimeout;
	private final BigInteger identifierSpaceSize;
	private final int bootstrapRequestPeerCount;

//-------------------------------------------------------------------	
	public GradientConfiguration(int shuffleLength, int randomViewSize, int similarViewSize, long shufflePeriod, long shuffleTimeout, BigInteger identifierSpaceSize, int bootstrapRequestPeerCount) {
		super();
		this.shuffleLength = shuffleLength;
		this.randomViewSize = randomViewSize;
		this.similarViewSize = similarViewSize;
		this.shufflePeriod = shufflePeriod;
		this.shuffleTimeout = shuffleTimeout;
		this.identifierSpaceSize = identifierSpaceSize;
		this.bootstrapRequestPeerCount = bootstrapRequestPeerCount;
	}

//-------------------------------------------------------------------	
	public int getShuffleLength() {
		return shuffleLength;
	}

//-------------------------------------------------------------------	
	public int getRandomViewSize() {
		return randomViewSize;
	}

//-------------------------------------------------------------------	
	public int getSimilarViewSize() {
		return similarViewSize;
	}

//-------------------------------------------------------------------	
	public long getShufflePeriod() {
		return shufflePeriod;
	}

//-------------------------------------------------------------------	
	public long getShuffleTimeout() {
		return shuffleTimeout;
	}

//-------------------------------------------------------------------	
	public BigInteger getIdentifierSpaceSize() {
		return identifierSpaceSize;
	}

//-------------------------------------------------------------------	
	public int getBootstrapRequestPeerCount() {
		return bootstrapRequestPeerCount;
	}

//-------------------------------------------------------------------	
	public void store(String file) throws IOException {
		Properties p = new Properties();
		p.setProperty("shuffle.length", "" + shuffleLength);
		p.setProperty("random.view.size", "" + randomViewSize);
		p.setProperty("similar.view.size", "" + similarViewSize);
		p.setProperty("shuffle.period", "" + shufflePeriod);
		p.setProperty("shuffle.timeout", "" + shuffleTimeout);
		p.setProperty("id.space.size", "" + identifierSpaceSize);
		p.setProperty("bootstrap.request.peer.count", "" + bootstrapRequestPeerCount);

		Writer writer = new FileWriter(file);
		p.store(writer, "se.sics.kompics.p2p.overlay.cyclon");
	}

//-------------------------------------------------------------------	
	public static GradientConfiguration load(String file) throws IOException {
		Properties p = new Properties();
		Reader reader = new FileReader(file);
		p.load(reader);

		int shuffleLength = Integer.parseInt(p.getProperty("shuffle.length"));
		int randomViewSize = Integer.parseInt(p.getProperty("random.view.size"));
		int similarViewSize = Integer.parseInt(p.getProperty("similar.view.size"));
		long shufflePeriod = Long.parseLong(p.getProperty("shuffle.period"));
		long shuffleTimeout = Long.parseLong(p.getProperty("shuffle.timeout"));
		BigInteger identifierSpaceSize = new BigInteger(p.getProperty("id.space.size"));
		int bootstrapRequestPeerCount = Integer.parseInt(p.getProperty("bootstrap.request.peer.count"));

		return new GradientConfiguration(shuffleLength, randomViewSize, similarViewSize, shufflePeriod, shuffleTimeout, identifierSpaceSize, bootstrapRequestPeerCount);
	}
}
