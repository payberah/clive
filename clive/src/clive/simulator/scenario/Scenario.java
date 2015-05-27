package clive.simulator.scenario;


import java.util.Random;

import clive.simulator.core.MSExecutionMain;
import clive.simulator.core.MSSimulationMain;

import se.sics.kompics.p2p.experiment.dsl.SimulationScenario;

public class Scenario {
	private static Random random;
	protected SimulationScenario scenario;

//-------------------------------------------------------------------
	public Scenario(SimulationScenario scenario) {
		this.scenario = scenario;
		this.scenario.setSeed(System.currentTimeMillis());
		random = scenario.getRandom();
	}

//-------------------------------------------------------------------
	public void setSeed(long seed) {
		this.scenario.setSeed(seed);
	}

//-------------------------------------------------------------------
	public void execute() {
		this.scenario.execute(MSExecutionMain.class);
	}

//-------------------------------------------------------------------
	public void simulate() {
		this.scenario.simulate(MSSimulationMain.class);
	}

//-------------------------------------------------------------------
	public static Random getRandom() {
		return random;
	}

//-------------------------------------------------------------------
	public static void setRandom(Random r) {
		random = r;
	}
}
