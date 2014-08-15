package org.ideasmashup.specialtactics.agents;

public interface Agent {

	public abstract void update();

	public abstract void destroy();

	public abstract boolean isDestroyed();
}
