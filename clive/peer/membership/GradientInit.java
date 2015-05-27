package clive.peer.membership;

import clive.peer.common.RandomView;
import se.sics.kompics.Init;

public final class GradientInit extends Init {

	private final GradientConfiguration configuration;
	private final RandomView randomView;
	private final int slots;

//-------------------------------------------------------------------
	public GradientInit(GradientConfiguration configuration, RandomView randomView, int slots) {
		super();
		this.configuration = configuration;
		this.randomView = randomView;
		this.slots = slots;
	}

//-------------------------------------------------------------------
	public GradientConfiguration getConfiguration() {
		return configuration;
	}

//-------------------------------------------------------------------
	public RandomView getRandomView() {
		return randomView;
	}

//-------------------------------------------------------------------
	public int getSlots() {
		return slots;
	}
}