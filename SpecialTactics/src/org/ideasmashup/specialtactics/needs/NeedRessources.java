package org.ideasmashup.specialtactics.needs;

import org.ideasmashup.specialtactics.needs.Needs.Types;


public class NeedRessources extends Need {

	protected int minerals;
	protected int gas;
	protected final Needs.Types[] types;

	public NeedRessources(int minerals, int gas) {
		super();
		this.types = new Types[]{Needs.Types.RESOURCES};
		this.minerals = minerals;
		this.gas = gas;
	}

	public NeedRessources(int minerals, int gas, float priority) {
		super(priority);
		this.types = new Types[]{Needs.Types.RESOURCES};
		this.minerals = minerals;
		this.gas = gas;
	}

	@Override
	public Types[] getTypes() {
		return types;
	}

	@Override
	public boolean canReceive(Object offer) {
		// accept all offers because current ressources values are
		// fetched directly from the Player instance so the "offer'
		// is ignored (and generally "null" except for special cases)
		return true;
	}
}
