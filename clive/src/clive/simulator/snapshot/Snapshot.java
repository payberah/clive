package clive.simulator.snapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import clive.main.Configuration;
import clive.peer.common.Buffer;
import clive.peer.common.MSPeerAddress;

public class Snapshot {
	private static DecimalFormat df = new DecimalFormat("#.###");
	private static HashMap<MSPeerAddress, PeerInfo> peers = new HashMap<MSPeerAddress, PeerInfo>();
	private static HashMap<MSPeerAddress, PeerInfo> helpers = new HashMap<MSPeerAddress, PeerInfo>();

	private static int counter = 0;
	private static int helperLastLoad = 0;
	private static int helperLoad = 0;
	private static int peersLastLoad = 0;
	private static int peersLoad = 0;
	private static int sourceLoad = 0;
	private static double netSize = 0;
	private static String FILENAME = "clive-join-homo-lcw90.out";
	
//-------------------------------------------------------------------
	public static void init() {
		FileIO.write("", FILENAME);
	}
	
//-------------------------------------------------------------------
	public static void addPeer(MSPeerAddress address, int bw, Buffer buffer, long birthTime) {
		peers.put(address, new PeerInfo(buffer, bw, birthTime));
	}

//-------------------------------------------------------------------
	public static void removePeer(MSPeerAddress address) {
		peers.remove(address);
	}

//-------------------------------------------------------------------
	public static void updateBuffer(MSPeerAddress address, Buffer buffer) {
		PeerInfo peerInfo = peers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.update(buffer);
	}
	
//-------------------------------------------------------------------
	public static void updateNum(MSPeerAddress address, double[] ratio) {
		PeerInfo peerInfo = peers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.updateRatio(ratio);
	}
	
//-------------------------------------------------------------------
	public static void addHelper(MSPeerAddress address) {
		helpers.put(address, new PeerInfo());
	}

//-------------------------------------------------------------------
	public static void removeHelper(MSPeerAddress address) {
		helpers.remove(address);
	}

//-------------------------------------------------------------------
	public static void helperUpdateParents(MSPeerAddress address, ArrayList<MSPeerAddress> parents) {
		PeerInfo peerInfo = helpers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.updateParents(parents);
	}

//-------------------------------------------------------------------
	public static void helperUpdateChildren(MSPeerAddress address, ArrayList<MSPeerAddress> children) {
		PeerInfo peerInfo = helpers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.updateChildren(children);
	}
	
//-------------------------------------------------------------------
	public static void updateParents(MSPeerAddress address, ArrayList<MSPeerAddress> parents) {
		PeerInfo peerInfo = peers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.updateParents(parents);
	}

//-------------------------------------------------------------------
	public static void updateChildren(MSPeerAddress address, ArrayList<MSPeerAddress> children) {
		PeerInfo peerInfo = peers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.updateChildren(children);
	}

//-------------------------------------------------------------------
	public static void updateLoad(MSPeerAddress address) {
		PeerInfo peerInfo = peers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.updateLoad();
		
		if (address.getPeerId().equals(Configuration.SOURCE_ID))
			sourceLoad++;
		else
			peersLoad++;
	}

//-------------------------------------------------------------------
	public static void updatePlayedBlocks(MSPeerAddress address, boolean result) {
		PeerInfo peerInfo = peers.get(address);

		if (peerInfo == null)
			return;

		peerInfo.incPlayedBlocks();
		if (!result)
			peerInfo.incMissedBlocks();		
	}

//-------------------------------------------------------------------
	public static void setTimeToJoin(MSPeerAddress address, long joinTime) {
		PeerInfo peerInfo = peers.get(address);

		if (peerInfo == null)
			return;

		peerInfo.setJoinTime(joinTime);
	}

//-------------------------------------------------------------------
	public static void updateHelperLoad(int load) {
		helperLoad = load;
	}

//-------------------------------------------------------------------
	public static int getNetSize() {
		return peers.size();
	}

//-------------------------------------------------------------------
	public static void updateNetSize(double size) {
		netSize = size;
	}

//-------------------------------------------------------------------
	public static void report() {
		String str = new String();
		str += "clive current time: " + counter + "\n";
		//str += reportDetailes();
		str += reportNetworkState();
		str += reportContinuetyIndext();
		str += reportPlaybackLatency();
		str += verifyNetworkSize();
		str += reportHelpers();
		str += "---\n";
		str += "src net size: " + counter + " " + netSize + "\n";
		str += "---\n";
		str += "helper total load: " + counter + " " + helperLoad + "\n";
		str += "helper load: " + counter + " " + (helperLoad - helperLastLoad) + "\n";
		str += "peers total load: " + counter + " " + peersLoad + "\n";
		str += "peers load: " + counter + " " + (peersLoad - peersLastLoad) + "\n";
		str += "source load: " + counter + " " + sourceLoad + "\n";
		str += "###\n";
		
		System.out.println(str);
		
		//System.out.println("current time: " + counter);
		FileIO.append(str, FILENAME);
		
		//generateGraphVizReport();
		peersLastLoad = peersLoad;
		helperLastLoad = helperLoad;
		counter++;
	}

//-------------------------------------------------------------------
	private static String reportNetworkState() {
		PeerInfo peerInfo;
		String str = new String("---\n");
		int totalNumOfPeers = peers.size() - 1;
		int uploadSlots = 0;
		int downloadSlots =Configuration.PARENT_SIZE * totalNumOfPeers;
		
		for (MSPeerAddress peer : peers.keySet()) {
			peerInfo = peers.get(peer);
			uploadSlots += peerInfo.getBW();
		}
			
		str += "total number of peers: " + counter + " " + totalNumOfPeers + "\n";
		str += "total upload slots: " + counter + " " + uploadSlots + "\n";
		str += "total download slots: " + counter + " " + downloadSlots + "\n";
				
		return str;		
	}
	
//-------------------------------------------------------------------
	private static String reportHelpers() {
		String str = new String("---\n");
		str += "total helper: " + counter + " " + helpers.size() + "\n";
		
//		for (MSPeerAddress peer : helpers.keySet()) {
//			peerInfo = helpers.get(peer);
//		
//			str += "helper: " + peer;
//			str += ", parent: " + peerInfo.getParents();
//			str += ", children: " + peerInfo.getChildren();
//			str += "\n";
//		}
		
		return str;
	}

//-------------------------------------------------------------------
	@SuppressWarnings("unused")
	private static String reportDetailes() {
		PeerInfo peerInfo;
		String str = new String("---\n");

		for (MSPeerAddress peer : peers.keySet()) {
			peerInfo = peers.get(peer);

			if (peerInfo.getTotalPlayedBlocks() > 0 && getContinuetyIndext(peerInfo) < 99) {
				str += "peer: " + peer;
				//str += ", load: " + peerInfo.getLoad();
				//str += ", bw: " + peerInfo.getBW();
				str += ", pp: " + peerInfo.getBuffer().getPlaybackPoint();
				if (peerInfo.getTotalPlayedBlocks() > 0)
					str += ", ci: " + df.format(getContinuetyIndext(peerInfo)) + "%";
				//str += ", parents: " + peerInfo.getParents();
				//str += ", children: " + peerInfo.getChildren();
				str += ", bm: " + peerInfo.getBuffer().getBM();
				str += "\n";
			}
		}
		
		return str;

//		if (peerInfo.getBuffer().getBM().size() > 0)
//		str += ", bm: " + peerInfo.getBuffer().getBM().get(0);
	}

//-------------------------------------------------------------------
	private static String reportPlaybackLatency() {
		String str = new String("---\n");
		long sourcePlaybackPoint = 0;
		int totalNumOfPeers = 0;
		long peerPlaybackLatency;
		long total = 0;
		long totalPeers = 0;

		for (MSPeerAddress peer : peers.keySet()) {
			if (peer.getPeerId().equals(Configuration.SOURCE_ID)) {
				sourcePlaybackPoint = peers.get(peer).getBuffer().getPlaybackPoint();
				break;
			}
		}
		
		for (MSPeerAddress peer : peers.keySet()) {
			if (peer.getPeerId().equals(Configuration.SOURCE_ID))
				continue;
			
			if (peers.get(peer).getBuffer().getPlaybackPoint() == -1)
				continue;

			totalNumOfPeers++;
		}
		
		if (totalNumOfPeers > 0) {
			for (MSPeerAddress peer : peers.keySet()) {
				if (peer.getPeerId().equals(Configuration.SOURCE_ID))
					continue;

				if (peers.get(peer).getBuffer().getPlaybackPoint() == -1)
					continue;

				peerPlaybackLatency = sourcePlaybackPoint - peers.get(peer).getBuffer().getPlaybackPoint();
			
				total += peerPlaybackLatency;

				totalPeers += peerPlaybackLatency;
				
			}
		
			double pl = (double)totalPeers / totalNumOfPeers;
			str += "avg. playback latency: " + counter + " " + df.format((double)(pl * Configuration.BLOCK_SIZE) / Configuration.STREAM_RATE) + "\n";
		}		

		return str;
	}

//-------------------------------------------------------------------
	private static String reportContinuetyIndext() {
		double index;
		String str = new String("---\n");
		int totalNumOfPeers = 0;
		
		double[] continuetyIndex = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
									0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
									0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
									0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
									0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
									0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
									0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
									0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
									0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
									0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		
		for (MSPeerAddress peer : peers.keySet()) {
			if (peer.getPeerId().equals(Configuration.SOURCE_ID))
				continue;
			
			if (peers.get(peer).getBuffer().getPlaybackPoint() == -1) 
				continue;

			index = getContinuetyIndext(peers.get(peer));

			totalNumOfPeers++;
				
			if (index == 100) {
				continuetyIndex[99]++;
				continuetyIndex[100]++;
			} else
				continuetyIndex[(int)Math.floor(index)]++;
		}
		
		if (totalNumOfPeers == 0)
			str += "number of streaming peers: " + counter + "\t" + totalNumOfPeers + "\n";
		else {
			str += "number of streaming peers: " + counter + "\t" + totalNumOfPeers + "\n";
			
			double ci = 0;
			for (int i = 0; i < 10; i++)
				ci += continuetyIndex[90 + i];
			str += "continuty index 99%: " + counter + " " + df.format((continuetyIndex[99] * 100) / totalNumOfPeers) + "%\n";
			str += "continuty index 100%: " + counter + " " + df.format((continuetyIndex[100] * 100) / totalNumOfPeers) + "%\n";
		}
		
		return str;
	}
	
//-------------------------------------------------------------------
	private static double getContinuetyIndext(PeerInfo peerInfo) {
		double continuetyIndex = 0;
		
		if (peerInfo.getTotalPlayedBlocks() > 0)
			continuetyIndex = (double)((peerInfo.getTotalPlayedBlocks() - peerInfo.getMissedBlocks()) * 100) / (double)peerInfo.getTotalPlayedBlocks();
		
		return continuetyIndex;
	}
	
//-------------------------------------------------------------------
	private static String verifyNetworkSize() {
		PeerInfo peerInfo;
		String str = new String("---\n");
		double[] total = new double[Configuration.SLOT_RANGE];
		double[] hitRatio = new double[Configuration.SLOT_RANGE];
		double[] realTotal = new double[Configuration.SLOT_RANGE];
		double[] realRatio = new double[Configuration.SLOT_RANGE];
		int count = 0;
		DecimalFormat df = new DecimalFormat("#.##");
		
		for (int i = 0; i < Configuration.SLOT_RANGE; i++) {
			total[i] = 0;
			hitRatio[i] = 0;
			realTotal[i] = 0;
			realRatio[i] = 0;
		}
				
		for (MSPeerAddress peer : peers.keySet()) {
			peerInfo = peers.get(peer);

			double[] d = peerInfo.getRatio();
			if (d == null)
				continue;
			
			for (int i = 0; i < Configuration.SLOT_RANGE; i++)
				total[i] += d[i];

			int realSlot = peerInfo.getBW();
			realTotal[realSlot] += 1;
			
			count++;
		}
		
		for (int i = 0; i < Configuration.SLOT_RANGE; i++) {
			hitRatio[i] = total[i] / count;
			realRatio[i] = realTotal[i] / count;
		}


//		str += "avg: " + df.format(total / count) + "\n";
		str += "real distribution: "; 
		for (int i = 0; i < Configuration.SLOT_RANGE; i++)
			str += df.format(realRatio[i]) + ", ";

		str += "\n";
		
		str += "distribution: "; 
		for (int i = 0; i < Configuration.SLOT_RANGE; i++)
			str += df.format(hitRatio[i]) + ", ";

		double maxDiff = 0;
		double diff = 0;
		
		
		for (int i = 0; i < Configuration.SLOT_RANGE; i++) {
			diff = Math.abs(realRatio[i] - hitRatio[i]);
			if (diff > maxDiff)
				maxDiff = diff;
		}
		
		str += "\n";
		str += "max diff: " + counter + " " + maxDiff;
		
		str += "\n";
		return str;
	}
}
