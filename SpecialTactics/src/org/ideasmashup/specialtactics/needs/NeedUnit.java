package org.ideasmashup.specialtactics.needs;

import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Units;

import bwapi.Unit;
import bwapi.UnitType;

public class NeedUnit extends Need {

	protected UnitType unittype;
	protected Units.Types types;

	public NeedUnit(Consumer owner, UnitType type) {
		super(owner);
		this.unittype = type;
		this.types = Units.Types.getType(type);
	}

	public NeedUnit(Consumer owner, UnitType type, float priority) {
		super(owner, priority);
		this.unittype = type;
		this.types = Units.Types.getType(type);
	}

	public NeedUnit(Consumer owner, UnitType type, float priority, Needs.Modifiers modifiers) {
		super(owner, priority, modifiers);
		this.unittype = type;
		this.types = Units.Types.getType(type);
	}

	public UnitType getUnitType() {
		return unittype;
	}

	public Units.Types getTypes() {
		return types;
	}

	public void setTypes(Units.Types types) {
		this.types = types;
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
