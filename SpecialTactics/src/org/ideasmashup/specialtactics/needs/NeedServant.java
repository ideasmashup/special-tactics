package org.ideasmashup.specialtactics.needs;

import bwapi.UnitType;

public class NeedServant extends Need {

	protected UnitType type;

	public NeedServant(UnitType type) {
		super();
		this.type = type;
	}

	public NeedServant(UnitType type, float priority) {
		super(priority);
		this.type = type;
	}

}
