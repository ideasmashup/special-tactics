package org.ideasmashup.specialtactics.agents.commands;

import java.util.LinkedList;

import org.ideasmashup.specialtactics.agents.UnitAgent;

import bwapi.Unit;

public class CommandChain extends UnitAgent {

	protected LinkedList<Command> commands;

	public CommandChain(Unit bindee, Command... commands) {
		super(bindee);
		this.commands = new LinkedList<Command>();
	}

	@Override
	public void update() {
		super.update();

		// iterate through commands and run each of them on the bindee Unit
		// and when one is completed, move on to the next
		nextCommand();
	}

	protected void nextCommand() {
		//
	}
}
