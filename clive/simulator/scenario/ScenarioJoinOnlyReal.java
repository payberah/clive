package clive.simulator.scenario;

import java.math.BigInteger;
import java.util.Random;

import clive.main.Configuration;

import se.sics.kompics.p2p.experiment.dsl.SimulationScenario;

@SuppressWarnings("serial")
public class ScenarioJoinOnlyReal extends Scenario {
	private static SimulationScenario scenario = new SimulationScenario() {{
		final Random random = getRandom();
		final SlotDistributionReal distro = new SlotDistributionReal(random);
		
		StochasticProcess sourceJoin = new StochasticProcess() {{
			eventInterArrivalTime(exponential(100));
			raise(1, Operations.sourceJoin(Configuration.SRC_FANOUT * (Configuration.STREAM_RATE / Configuration.BW_UNIT)));
		}};

		StochasticProcess helperJoin = new StochasticProcess() {{
			eventInterArrivalTime(exponential(100));
			raise(1, Operations.passiveHelperJoin());
		}};
		
		StochasticProcess class1 = new StochasticProcess() {{
			eventInterArrivalTime(exponential(10));
			raise(146, Operations.msJoin(Configuration.PARENT_SIZE), constant(new BigInteger(20 + "")), uniform(13));
		}};

		StochasticProcess class2 = new StochasticProcess() {{
			eventInterArrivalTime(exponential(10));
			raise(58, Operations.msJoin(Configuration.PARENT_SIZE), distro, uniform(13));
		}};

		StochasticProcess class3 = new StochasticProcess() {{
			eventInterArrivalTime(exponential(10));
			raise(94, Operations.msJoin(Configuration.PARENT_SIZE), constant(new BigInteger(2 + "")), uniform(13));
		}};

		StochasticProcess class4 = new StochasticProcess() {{
			eventInterArrivalTime(exponential(10));
			raise(209, Operations.msJoin(Configuration.PARENT_SIZE), constant(BigInteger.ONE), uniform(13));
		}};

		StochasticProcess class5 = new StochasticProcess() {{
			eventInterArrivalTime(exponential(10));
			raise(493, Operations.msJoin(Configuration.PARENT_SIZE), constant(BigInteger.ZERO), uniform(13));
		}};

		sourceJoin.start();
		helperJoin.startAfterTerminationOf(10, sourceJoin);
		class1.startAfterTerminationOf(2000, sourceJoin);
		class2.startAfterTerminationOf(2500, class1);
		class3.startAfterTerminationOf(2500, class1);
		class4.startAfterTerminationOf(2500, class1);
		class5.startAfterTerminationOf(2500, class1);
	}};
	
//-------------------------------------------------------------------
	public ScenarioJoinOnlyReal() {
		super(scenario);
	} 
}
