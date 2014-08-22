package org.ideasmashup.specialtactics.needs;

import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Needs.Types;

public class NeedSupply extends Need {

	protected int supply;
	protected final Needs.Types[] types;

	public NeedSupply(Consumer owner, int supply) {
		super(owner);
		this.types = new Types[]{Needs.Types.SUPPLY};
		this.supply = supply;
	}

	public NeedSupply(Consumer owner, int supply, float priority) {
		super(owner, priority);
		this.types = new Types[]{Needs.Types.SUPPLY};
		this.supply = supply;
	}

	public NeedSupply(Consumer owner, int supply, float priority, Needs.Modifiers modifiers) {
		super(owner, priority, modifiers);
		this.types = new Types[]{Needs.Types.SUPPLY};
		this.supply = supply;
	}

	@Override
	public Types[] getTypes() {
		return types;
	}

	@Override
	public boolean canReceive(Object offer) {
		// accept all offers because current supply value is
		// fetched directly from the Player instance so the "offer'
		// is ignored (and generally "null" except for special cases)
		return true;
	}
}
