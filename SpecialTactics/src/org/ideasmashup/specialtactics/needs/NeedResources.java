package org.ideasmashup.specialtactics.needs;

import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.managers.Needs;


public class NeedResources extends Need {

	protected int minerals;
	protected int gas;

	public NeedResources(Consumer owner, int minerals, int gas) {
		super(owner);
		this.minerals = minerals;
		this.gas = gas;
	}

	public NeedResources(Consumer owner, int minerals, int gas, float priority) {
		super(owner, priority);
		this.minerals = minerals;
		this.gas = gas;
	}

	public NeedResources(Consumer owner, int minerals, int gas, float priority, Needs.Modifiers modifiers) {
		super(owner, priority, modifiers);
		this.minerals = minerals;
		this.gas = gas;
	}

	@Override
	public boolean canReceive(Object offer) {
		// accept all offers because current ressources values are
		// fetched directly from the Player instance so the "offer'
		// is ignored (and generally "null" except for special cases)
		return true;
	}
}
