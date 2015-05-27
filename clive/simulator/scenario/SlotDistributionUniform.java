package clive.simulator.scenario;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import se.sics.kompics.p2p.experiment.dsl.distribution.Distribution;

public class SlotDistributionUniform extends Distribution<BigInteger> {
	/*
	 * 54% of peers: 0 children
	 * 22% of peers: 1 children
	 * 10% of peers: 2 children
	 * 6% of peers: 3-19 children
	 * 8% of peers: 20 children
	 */
	
	private Random random;
	private static final long serialVersionUID = 6853092446046319743L;

	public SlotDistributionUniform(Random random) {
		super(Type.OTHER, BigInteger.class);
		this.random = random;
	}

//-------------------------------------------------------------------
	public static int getUtilityLevel(int utilityValue) {
		int utilityLevel;
		
		
		if (utilityValue == 1)
			utilityLevel = 1;
		else if (utilityValue == 2)
			utilityLevel = 2;
		else if (utilityValue == 3)
			utilityLevel = 3;
		else if (utilityValue == 4)
			utilityLevel = 4;
		else if (utilityValue == 5)
			utilityLevel = 5;
		else if (utilityValue == 6)
			utilityLevel = 6;
		else if (utilityValue == 7)
			utilityLevel = 7;
		else if (utilityValue == 8)
			utilityLevel = 8;
		else if (utilityValue == 9)
			utilityLevel = 9;
		else if (utilityValue == 10)
			utilityLevel = 10;
		else 
			utilityLevel = 11;
		
		return utilityLevel;
	}
	
//-------------------------------------------------------------------
	public static int getHighestUtilityLevel() {
		return 11;
	}
	
//-------------------------------------------------------------------
	@Override
	public final BigInteger draw() {
		int num;
		double r = random.nextDouble();
		
		if (r < 0.1)
			num = 4;
		else if (r < 0.2)
			num = 5;
		else if (r < 0.3)
			num = 6;
		else if (r < 0.4)
			num = 7;
		else if (r < 0.5)
			num = 8;
		else if (r < 0.6)
			num = 9;
		else if (r < 0.7)
			num = 10;
		else if (r < 0.8)
			num = 11;
		else if (r < 0.9)
			num = 12;
		else 
			num = 13;
		
//		if (r < 0.25)
//			num = 1;
//		else if (r < 0.5)
//			num = 2;
//		else if (r < 0.75)
//			num = 3;
//		else 
//			num = 4;
//		
		return new BigDecimal(num).toBigInteger();		
	}
	
	public static void main(String args[]) {
		int num;
		int[] d = {0, 0, 0, 0, 0, 0, 0, 0};
		SlotDistributionUniform md = new SlotDistributionUniform(new Random());
		
		for (int i = 0; i < 100; i++) {
			num = md.draw().intValue();
			System.out.println(num);

			if (num == 1)
				d[0]++;
			else if (num == 2)
				d[1]++;
			else if (num == 3)
				d[2]++;
			else if (num == 4)
				d[3]++;
			else if (num == 5)
				d[4]++;
			else if (num == 6)
				d[5]++;
			else if (num == 7)
				d[6]++;
			else 
				d[7]++;
		}
		
		for (int i = 0; i < 8; i++)
			System.out.println("num of peer in group " + i + ": " + d[i]);
	}
}
