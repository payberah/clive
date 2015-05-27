package clive.simulator.scenario;

import java.math.BigInteger;

import clive.simulator.core.MSPeerFail;
import clive.simulator.core.MSPeerGetPeer;
import clive.simulator.core.MSPeerJoin;
import clive.simulator.core.MSZeroJoin;
import clive.simulator.core.PassiveHelperJoin;
import clive.simulator.core.SourceJoin;

import se.sics.kompics.p2p.experiment.dsl.adaptor.Operation;
import se.sics.kompics.p2p.experiment.dsl.adaptor.Operation1;
import se.sics.kompics.p2p.experiment.dsl.adaptor.Operation2;

@SuppressWarnings("serial")
public class Operations {

//-------------------------------------------------------------------
	static Operation2<MSPeerJoin, BigInteger, BigInteger> msJoin(final int agents) {
		return new Operation2<MSPeerJoin, BigInteger, BigInteger>() {
			public MSPeerJoin generate(BigInteger num, BigInteger id) {
				return new MSPeerJoin(id, num.intValue(), num.intValue(), agents);
			}
		};
	}

//-------------------------------------------------------------------
	static Operation2<MSZeroJoin, BigInteger, BigInteger> msZero(final int agents) {
		return new Operation2<MSZeroJoin, BigInteger, BigInteger>() {
			public MSZeroJoin generate(BigInteger num, BigInteger id) {
				return new MSZeroJoin(id, num.intValue(), agents);
			}
		};
	}
//-------------------------------------------------------------------
	static Operation<SourceJoin> sourceJoin(final long agents) {
		return new Operation<SourceJoin>() {
			public SourceJoin generate() {
				return new SourceJoin(agents);
			}
		};
	}
	
//-------------------------------------------------------------------
	static Operation<PassiveHelperJoin> passiveHelperJoin() {
		return new Operation<PassiveHelperJoin>() {
			public PassiveHelperJoin generate() {
				return new PassiveHelperJoin();
			}
		};
	}

//-------------------------------------------------------------------
//	static Operation1<SourceJoin, Long> sourceJoin = new Operation1<SourceJoin, Long>() {
//		public SourceJoin generate(Long seats) {
//			return new SourceJoin(seats);
//		}
//	};

	
//-------------------------------------------------------------------
	static Operation1<MSPeerFail, BigInteger> msFail = new Operation1<MSPeerFail, BigInteger>() {
		public MSPeerFail generate(BigInteger id) {
			return new MSPeerFail(id);
		}
	};

//-------------------------------------------------------------------
	static Operation1<MSPeerGetPeer, BigInteger> msGetPeer = new Operation1<MSPeerGetPeer, BigInteger>() {
		public MSPeerGetPeer generate(BigInteger id) {
			return new MSPeerGetPeer(id);
		}
	};
}
