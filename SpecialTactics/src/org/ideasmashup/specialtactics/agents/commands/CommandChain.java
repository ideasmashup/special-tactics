package org.ideasmashup.specialtactics.agents.commands;

import java.util.LinkedList;

import org.ideasmashup.specialtactics.managers.Units;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitCommandType;

public class CommandChain extends Command implements CommandListener {

	protected LinkedList<Command> commands;
	protected Command current;

	public CommandChain(Unit bindee, Command... commands) {
		super(bindee);
		this.current = null;
		this.commands = new LinkedList<Command>();
		for (Command command : commands) {
			this.commands.add(command);
		}
	}

	@Override
	public void update() {
		super.update();

		// iterate through commands and run each of them on the bindee Unit
		// and when one is completed, move on to the next
		nextCommand();
	}

	protected void nextCommand() {
		if (!commands.isEmpty()) {
			// fetch command and run it
			current = commands.pollFirst();
			current.addListener(this);
			current.start();
		}
		else {
			// last command here!
		}
	}

	@Override
	public void onDone(Object result, boolean success) {
		// command done, move onto next one
		Command next = commands.peekFirst();
		if (next.wantForward()) {
			// this result should be passed to the next command
			// so store it for he next in line to use !!
		}
	}

	@Override
	public void onFail() {
		//

	}
}
