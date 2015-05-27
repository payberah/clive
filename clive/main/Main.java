package clive.main;

import clive.simulator.scenario.Scenario;
import clive.simulator.scenario.ScenarioJoinOnly;

public class Main {
	public static void main(String[] args) throws Throwable {
		Configuration configuration = new Configuration();
		configuration.set();
		
		Scenario scenario = new ScenarioJoinOnly();
		scenario.setSeed(5);
		scenario.simulate();
	}
}
