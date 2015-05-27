package clive.simulator.scenario;

import java.util.Random;

import clive.main.Configuration;

import se.sics.kompics.p2p.experiment.dsl.SimulationScenario;

@SuppressWarnings("serial")
public class ScenarioChurn extends Scenario {
	private static SimulationScenario scenario = new SimulationScenario() {{
		final Random random = getRandom();
		//final SlotDistributionHomo distro = new SlotDistributionHomo(random);
		final SlotDistributionUniform distro = new SlotDistributionUniform(random);
		
		StochasticProcess sourceJoin = new StochasticProcess() {{
			eventInterArrivalTime(exponential(100));
			raise(1, Operations.sourceJoin(Configuration.SRC_FANOUT * (Configuration.STREAM_RATE / Configuration.BW_UNIT)));
		}};

		StochasticProcess helperJoin = new StochasticProcess() {{
			eventInterArrivalTime(exponential(100));
			raise(1, Operations.passiveHelperJoin());
		}};
		
		StochasticProcess nodesJoin = new StochasticProcess() {{
			eventInterArrivalTime(exponential(10));
			raise(1000, Operations.msJoin(Configuration.PARENT_SIZE), distro, uniform(13));
		}};

		StochasticProcess churn = new StochasticProcess() {{
			eventInterArrivalTime(exponential(1000));
			raise(1000, Operations.msJoin(Configuration.PARENT_SIZE), distro, uniform(13));
			raise(1000, Operations.msFail, uniform(13));
		}};
		
		sourceJoin.start();
		helperJoin.startAfterTerminationOf(10, sourceJoin);
		nodesJoin.startAfterTerminationOf(2000, sourceJoin);
		churn.startAfterTerminationOf(20000, nodesJoin);
	}};
	
//-------------------------------------------------------------------
	public ScenarioChurn() {
		super(scenario);
	} 
}
