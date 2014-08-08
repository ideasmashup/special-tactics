package org.ideasmashup.specialtactics.agents;

import bwapi.Unit;



public class Agent {

	protected Unit bindee;

	public Agent(Unit bindee) {
		this.bindee = bindee;

		init();
	}

	protected void init() {
		// first initialization (on creation)

	}

	public void update() {
		// called on every frame (or as frequently as possible)

	}

}
