package org.ideasmashup.specialtactics.agents;

import java.util.List;

import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Units;

import bwapi.Unit;
import bwapi.UnitType;

public class MasterAgent extends UnitAgent {

	protected Units.Types servantsType;
	protected List<Unit> servants;

	public MasterAgent(Unit bindee) {
		super(bindee);
	}

	protected void addServant(Unit unit) {
		this.servants.add(unit);
	}

	protected void freeServant(Unit unit) {
		servants.remove(unit);

		// freed units are like "new" units
		Units.getInstance().onUnitComplete(unit);
	}

	protected void freeAllServants() {
		for (Unit servant : servants) {
			Units.getInstance().onUnitComplete(servant);
		}
		servants.clear();
	}

	public UnitType getServantsType() {
		return servantsType.getUnitType();
	}

}