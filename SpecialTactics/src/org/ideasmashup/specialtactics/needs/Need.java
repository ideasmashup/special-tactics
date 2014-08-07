package org.ideasmashup.specialtactics.needs;

public class Need {

	protected boolean isSatisfied;
	protected float priority;

	public Need() {
		this.isSatisfied = false;
		this.priority = 0;
	}

	public Need(float priority) {
		this();
		this.priority = priority;
	}

	public boolean isSatisfied() {
		return isSatisfied;
	}

	public void setSatified(boolean satisfied) {
		this.isSatisfied = satisfied;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public float getPriority() {
		return this.priority;
	}

}
