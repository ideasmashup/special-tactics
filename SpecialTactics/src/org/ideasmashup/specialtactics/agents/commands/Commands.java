package org.ideasmashup.specialtactics.agents.commands;

public class Commands {
	public static enum States {
		PAUSED,
		RUNNING,
		WAITING_RESULT,
		FAILURE,
		SUCCESS,
		STOPPED
	}
}
