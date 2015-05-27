package clive.main;


import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import clive.peer.membership.GradientConfiguration;
import clive.peer.mspeers.MSConfiguration;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.NetworkConfiguration;
import se.sics.kompics.network.Transport;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;

public class Configuration {
	public static BigInteger SOURCE_ID = BigInteger.ZERO;
	public static BigInteger PASSIVE_HELPER_ID = BigInteger.ONE;

	public static double STRIPES = 1.1; // 1.2, buffer: 10000
	public static int WINDOW_SIZE = 200;
	public static int PARENT_SIZE = 8;	
	public static int MAX_HELPER_CHILDREN = 2;
	public static int STREAM_RATE = 500;
	public static int BW_UNIT = 100;
	public static int BLOCK_SIZE = 20;
	public static int SRC_FANOUT = 10;
	public static int ACTIVE_HELPER_FANOUT = 20;
	public static int PASSIVE_HELPER_FANOUT = 100000;
	public static int BUFFER_HELP_NEEDED_SIZE = 90;
	public static int AGGREGATION_PERIOD = 3000;
	public static int SLOT_RANGE = 100;
	
	int networkPort = 8081;
	int webPort = 8080;
	int bootId = Integer.MAX_VALUE;

	public InetAddress ip = null;
	{
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
		}
	}

	Address bootServerAddress = new Address(ip, networkPort, bootId);

	BootstrapConfiguration bootConfiguration = new BootstrapConfiguration(bootServerAddress, 6000000, 4000, 3, 30000, webPort, webPort);
	PingFailureDetectorConfiguration fdConfiguration = new PingFailureDetectorConfiguration(10000, 50000, 5000, 1000, Transport.TCP);
	GradientConfiguration gradientConfiguration = new GradientConfiguration(5, 10, 10, 1000, 3000, new BigInteger("2").pow(13), 20);	
	MSConfiguration msConfiguration = new MSConfiguration(BW_UNIT, STREAM_RATE, BLOCK_SIZE, 300, 300, 5000, 15000, 50000, 1000, 30, 5000);
	NetworkConfiguration networkConfiguration = new NetworkConfiguration(ip, networkPort, 0);

//-------------------------------------------------------------------	
	public void set() throws IOException {
		String c = File.createTempFile("bootstrap.", ".conf").getAbsolutePath();
		bootConfiguration.store(c);
		System.setProperty("bootstrap.configuration", c);

		c = File.createTempFile("gradient.", ".conf").getAbsolutePath();
		gradientConfiguration.store(c);
		System.setProperty("gradient.configuration", c);

		c = File.createTempFile("ping.fd.", ".conf").getAbsolutePath();
		fdConfiguration.store(c);
		System.setProperty("ping.fd.configuration", c);
		
		c = File.createTempFile("ms.", ".conf").getAbsolutePath();
		msConfiguration.store(c);
		System.setProperty("ms.configuration", c);

		c = File.createTempFile("network.", ".conf").getAbsolutePath();
		networkConfiguration.store(c);
		System.setProperty("network.configuration", c);
	}
}
