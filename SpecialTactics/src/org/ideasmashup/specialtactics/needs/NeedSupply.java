package org.ideasmashup.specialtactics.needs;

import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.managers.Needs;

public class NeedSupply extends Need {

	protected int supply;

	public NeedSupply(Consumer owner, int supply) {
		super(owner);
		this.supply = supply;
	}

	public NeedSupply(Consumer owner, int supply, float priority) {
		super(owner, priority);
		this.supply = supply;
	}

	public NeedSupply(Consumer owner, int supply, float priority, Needs.Modifiers modifiers) {
		super(owner, priority, modifiers);
		this.supply = supply;
	}

	@Override
	public boolean canReceive(Object offer) {
		// accept all offers because current supply value is
		// fetched directly from the Player instance so the "offer'
		// is ignored (and generally "null" except for special cases)
		return true;
	}

	public int getSupply() {
		return supply;
	}
}
