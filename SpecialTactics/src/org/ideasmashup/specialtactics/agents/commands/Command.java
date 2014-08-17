package org.ideasmashup.specialtactics.agents.commands;

import java.util.LinkedList;

import org.ideasmashup.specialtactics.agents.UnitAgent;
import org.ideasmashup.specialtactics.managers.Agents;

import bwapi.Unit;
import bwapi.UnitCommand;

public class Command extends UnitAgent {

	protected Commands.States state;
	protected UnitCommand command;
	protected Object[] args;
	protected LinkedList<CommandListener> ls;
	protected int maxRetries;

	public Command(Unit bindee, UnitCommand command, Object... args) {
		super(bindee);
		this.ls = new LinkedList<CommandListener>();
		this.maxRetries = 100;
		this.state = Commands.States.PAUSED;
		this.command = command;
		this.args = args;
	}

	@Override
	public void update() {
		// only run the command if it's not paused
		if (state == Commands.States.RUNNING) {
			super.update();

			// run the command until it works
			if (bindee.canIssueCommand(command)) {
				if (bindee.issueCommand(command)) {
					// successful command
					waitForResult();
				}
				else {
					// unsuccessful -> will retry a few times before failing
					if (--maxRetries == 0) {
						fail(null);
					}
				}
			}
		}
	}

	protected void waitForResult() {
		this.state = Commands.States.WAITING_RESULT;
	}

	public boolean addListener(CommandListener ls) {
		return this.ls.add(ls);
	}

	public boolean removedListener(CommandListener ls) {
		return this.ls.remove(ls);
	}

	public Commands.States getState() {
		return state;
	}

	public void start() {
		// start and register agent if necessary
		if (!Agents.getInstance().contains(this)) {
			Agents.getInstance().add(this);
		}
		this.state = Commands.States.RUNNING;
	}

	public void pause() {
		this.state = Commands.States.PAUSED;
	}

	protected void fail(Object result) {
		//
		this.state = Commands.States.FAILURE;

		for (CommandListener l : ls) {
			l.onDone(result, false);
		}

		destroy();
	}

	protected void success(Object result) {
		//
		this.state = Commands.States.SUCCESS;

		for (CommandListener l : ls) {
			l.onDone(result, true);
		}

		destroy();
	}

	public void stop() {
		this.state = Commands.States.STOPPED;

		destroy();
	}

	@Override
	public void destroy() {
		super.destroy();
		this.ls = null;
	}
}
