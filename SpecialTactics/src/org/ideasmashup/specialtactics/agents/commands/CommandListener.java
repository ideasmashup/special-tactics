package org.ideasmashup.specialtactics.agents.commands;

public interface CommandListener {
	public abstract void onDone(Object result, boolean success);
	public abstract void onFail();
}
