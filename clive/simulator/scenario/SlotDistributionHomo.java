package clive.simulator.scenario;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import se.sics.kompics.p2p.experiment.dsl.distribution.Distribution;

public class SlotDistributionHomo extends Distribution<BigInteger> {
	private static final long serialVersionUID = 6853092446046319743L;

	public SlotDistributionHomo(Random random) {
		super(Type.OTHER, BigInteger.class);
	}

//-------------------------------------------------------------------
	@Override
	public final BigInteger draw() {
		int num = 8;
	
		return new BigDecimal(num).toBigInteger();		
	}
}
