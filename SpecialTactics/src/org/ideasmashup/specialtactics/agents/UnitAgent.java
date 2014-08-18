package org.ideasmashup.specialtactics.agents;

import bwapi.Unit;



public class UnitAgent extends DefaultAgent {

	protected Unit bindee;

	public UnitAgent(Unit bindee) {
		super();
		this.bindee = bindee;
	}

}
