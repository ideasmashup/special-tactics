package org.ideasmashup.specialtactics.needs;

import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Needs.Types;

import bwapi.Unit;
import bwapi.UnitType;

public class NeedUnit extends Need {

	protected UnitType unittype;
	protected final Types[] types;

	public NeedUnit(UnitType type) {
		super();
		this.types = new Types[]{Needs.Types.UNIT};
		this.unittype = type;
	}

	public NeedUnit(UnitType type, float priority) {
		super(priority);
		this.types = new Types[]{Needs.Types.UNIT};
		this.unittype = type;
	}

	@Override
	public Types[] getTypes() {
		return types;
	}

	@Override
	public boolean canReceive(Object offer) {
		if (offer instanceof Unit) {
			// only accept units "offers"
			return true;
		}
		return false;
	}

}
