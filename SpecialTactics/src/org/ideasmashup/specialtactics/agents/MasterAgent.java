package org.ideasmashup.specialtactics.agents;

import java.util.List;

import bwapi.Unit;

public class MasterAgent extends Agent {

	protected List<Unit> servants;

	public MasterAgent(Unit bindee) {
		super(bindee);
	}

	protected void addServant(Unit unit) {
		this.servants.add(unit);
	}

	protected void freeServant(Unit unit) {
		servants.remove(unit);
		// Utils.getProductChannels().add(unit);
	}

	protected void freeAllServants() {
	
	}

}