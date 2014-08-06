package org.ideasmashup.specialtactics.needs;

public class Need {

	protected boolean isSatisfied;
	protected int priority;

	public Need() {
		this.isSatisfied = false;
		this.priority = 0;
	}

	public Need(int priority) {
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

	public int getPriority() {
		return this.priority;
	}

}
