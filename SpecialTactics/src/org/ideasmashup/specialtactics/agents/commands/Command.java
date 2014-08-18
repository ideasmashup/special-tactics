package org.ideasmashup.specialtactics.agents.commands;

import java.util.LinkedList;

import org.ideasmashup.specialtactics.agents.UnitAgent;
import org.ideasmashup.specialtactics.managers.Agents;

import bwapi.Unit;
import bwapi.UnitCommand;
import bwapi.UnitCommandType;

public class Command extends UnitAgent {

	protected Commands.States state;
	protected UnitCommandType uct;
	protected Object[] args;
	protected LinkedList<CommandListener> ls;
	protected int maxRetries;

	protected boolean givesObject; // will return an Object to listeners?
	protected boolean wantForward; // must get returned Object of prev command?

	public Command(Unit bindee) {
		super(bindee);
		this.ls = new LinkedList<CommandListener>();
		this.maxRetries = 100;
		this.uct = null;
		this.args = new Object[0];
		this.state = Commands.States.PAUSED;
	}

	public Command(Unit bindee, UnitCommandType uct, Object... args) {
		this(bindee);
		this.uct = uct;
		this.args = args;
	}

	@Override
	public void update() {
		// only run the command if it's not paused
		if (state == Commands.States.RUNNING) {
			super.update();

			// run the command until it works
			if (bindee.canIssueCommand(uct)) {
				if (bindee.issueCommand(uct)) {
					// successful command
					startWaitingForResult();
				}
				else {
					// unsuccessful -> will retry a few times before failing
					if (--maxRetries == 0) {
						fail(null);
					}
				}
			}
		}
		else if (state == Commands.States.WAITING_RESULT) {
			// we are waiting for something
		}
	}

	protected void startWaitingForResult() {
		// wait for result of for command to be completed : example move
		// commands will return the final Position and wait until the unit
		// reaches the specified coordinates

		this.state = Commands.States.WAITING_RESULT;

		// depending on CommandType maube we have a result already or maybe
		// we need to wait for an event to obtain the actual result of the
		// command

		// see : https://code.google.com/p/bwapi/wiki/UnitCommand
		Commands.Types type = Commands.Types.fromUnitCommandType(uc.getType());
		if (type == Commands.Types.ENDS_AT_POSITION) {
			// move commands  : result is final Position when it is reached
			//      examples  : move, attack-move
		}
		else if (type == Commands.Types.ENDS_AFTER_TRANSFORM) {
			// morph commands : result is new Unit after morph/state change
			//       examples : siage-mode, lurker morph, etc
		}
		else if (type == Commands.Types.ENDS_NEAR_UNIT) {
			// join commands  : move close to another unit
			//      examples  : follow
		}
	}

	public UnitCommand getCommand() {
		return uc;
	}

	public Object[] getArgs() {
		return args;
	}

	public boolean givesResult() {
		return givesObject;
	}

	public boolean wantForward() {
		return wantForward;
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
