package org.ideasmashup.specialtactics.agents;

import bwapi.Unit;



public class UnitAgent extends DefaultAgent {

	protected Unit bindee;

	public UnitAgent(Unit bindee) {
		this.bindee = bindee;
		this.dead = false;

		init();
	}

	protected void init() {
		// first initialization (on creation)
	}

}
