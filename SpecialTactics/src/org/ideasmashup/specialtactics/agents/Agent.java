package org.ideasmashup.specialtactics.agents;

import bwapi.Unit;



public class Agent {

	protected Unit bindee;
	protected boolean dead;

	public Agent(Unit bindee) {
		this.bindee = bindee;
		this.dead = false;

		init();
	}

	protected void init() {
		// first initialization (on creation)

	}

	public void update() {
		// called on every frame (or as frequently as possible)

	}

	public void destroy() {
		// kill this agent
		this.dead = true;
	}

	public boolean isDestroyed() {
		return dead;
	}

}
