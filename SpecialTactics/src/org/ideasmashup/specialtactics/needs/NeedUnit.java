package org.ideasmashup.specialtactics.needs;

import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.managers.Needs;

import bwapi.Unit;
import bwapi.UnitType;

public class NeedUnit extends Need {

	protected UnitType unittype;

	public NeedUnit(Consumer owner, UnitType type) {
		super(owner);
		this.unittype = type;
	}

	public NeedUnit(Consumer owner, UnitType type, float priority) {
		super(owner, priority);
		this.unittype = type;
	}

	public NeedUnit(Consumer owner, UnitType type, float priority, Needs.Modifiers modifiers) {
		super(owner, priority, modifiers);
		this.unittype = type;
	}

	public UnitType getUnitType() {
		return unittype;
	}

	@Override
	public boolean canReceive(Object offer) {
		if (offer instanceof Unit) {
			// only accept units "offers" of the correct type
			Unit unit = (Unit) offer;
			return unit.getType() == this.unittype;
		}
		return false;
	}

}
