package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.needs.Need;

import bwapi.Unit;

public class StructureBuildingAgent extends MasterAgent implements Consumer {

	public StructureBuildingAgent(Unit bindee) {
		super(bindee);
	}

	@Override
	public Need[] getNeeds(boolean returnAll) {
		return null;
	}

	@Override
	public boolean fillNeeds(Object offer) {
		return false;
	}

}
