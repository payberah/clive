/**
 * This file is part of the Kompics P2P Framework.
 * 
 * Copyright (C) 2009 Swedish Institute of Computer Science (SICS)
 * Copyright (C) 2009 Royal Institute of Technology (KTH)
 *
 * Kompics is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package clive.simulator.core;


import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

import clive.peer.membership.GradientConfiguration;
import clive.peer.mspeers.MSConfiguration;

import se.sics.kompics.ChannelFilter;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Kompics;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.NetworkConfiguration;
import se.sics.kompics.network.model.king.KingLatencyMap;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.bootstrap.server.BootstrapServer;
import se.sics.kompics.p2p.bootstrap.server.BootstrapServerInit;
import se.sics.kompics.p2p.experiment.dsl.SimulationScenario;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;
import se.sics.kompics.p2p.orchestrator.P2pOrchestrator;
import se.sics.kompics.p2p.orchestrator.P2pOrchestratorInit;
import se.sics.kompics.timer.Timer;

public final class MSExecutionMain extends ComponentDefinition {
	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	
	private static SimulationScenario scenario = SimulationScenario.load(System.getProperty("scenario"));

	public static void main(String[] args) {
		Kompics.createAndStart(MSExecutionMain.class, 8);
	}

	public MSExecutionMain() throws InterruptedException, IOException {
		P2pOrchestrator.setSimulationPortType(MSSimulatorPort.class);

		// create
		Component p2pOrchestrator = create(P2pOrchestrator.class);
		Component bootstrapServer = create(BootstrapServer.class);
		Component msSimulator = create(MSSimulator.class);

		// loading component configurations
		final BootstrapConfiguration bootConfiguration = BootstrapConfiguration.load(System.getProperty("bootstrap.configuration"));
		final GradientConfiguration gradientConfiguration = GradientConfiguration.load(System.getProperty("gradient.configuration"));
		final NetworkConfiguration networkConfiguration = NetworkConfiguration.load(System.getProperty("network.configuration"));
		final MSConfiguration msConfiguration = MSConfiguration.load(System.getProperty("ms.configuration"));
		final PingFailureDetectorConfiguration fdConfiguration = PingFailureDetectorConfiguration.load(System.getProperty("ping.fd.configuration"));

		Thread.sleep(2000);

		trigger(new P2pOrchestratorInit(scenario, new KingLatencyMap()), p2pOrchestrator.getControl());
		trigger(new BootstrapServerInit(bootConfiguration), bootstrapServer.getControl());
		trigger(new MSSimulatorInit(msConfiguration, bootConfiguration, gradientConfiguration, fdConfiguration, networkConfiguration.getAddress()), msSimulator.getControl());

		final class MessageDestinationFilter extends
				ChannelFilter<Message, Address> {
			public MessageDestinationFilter(Address address) {
				super(Message.class, address, true);
			}

			public Address getValue(Message event) {
				return event.getDestination();
			}
		}

		// connect
		connect(bootstrapServer.getNegative(Network.class), p2pOrchestrator.getPositive(Network.class), new MessageDestinationFilter(bootConfiguration.getBootstrapServerAddress()));
		connect(bootstrapServer.getNegative(Timer.class), p2pOrchestrator.getPositive(Timer.class));

		connect(msSimulator.getNegative(Network.class), p2pOrchestrator.getPositive(Network.class));
		connect(msSimulator.getNegative(Timer.class), p2pOrchestrator.getPositive(Timer.class));

		connect(msSimulator.getNegative(MSSimulatorPort.class), p2pOrchestrator.getPositive(MSSimulatorPort.class));
	}
}
