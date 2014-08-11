package org.ideasmashup.specialtactics.needs;

import bwapi.UnitType;

public class NeedUnit extends Need {

	protected UnitType type;

	public NeedUnit(UnitType type) {
		super();
		this.type = type;
	}

	public NeedUnit(UnitType type, float priority) {
		super(priority);
		this.type = type;
	}

}
