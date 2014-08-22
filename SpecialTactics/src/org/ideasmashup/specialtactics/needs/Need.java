package org.ideasmashup.specialtactics.needs;

import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.managers.Needs;


public abstract class Need {

	protected boolean isSatisfied;
	protected float priority;
	protected Needs.Modifiers modifiers;
	protected Consumer owner;

	// preset priorities levels
	public static float CRITICAL = -Float.MAX_VALUE;
	public static float HIGHEST  = -888f;
	public static float HIGHER   = -88f;
	public static float HIGH     = -8f;
	public static float NORMAL   =  0f;
	public static float LOW      =  8f;
	public static float LOWER    =  88f;
	public static float LOWEST   =  888f;
	public static float USELESS  =  Float.MAX_VALUE;

	public Need(Consumer owner) {
		this.owner = owner;
		this.isSatisfied = false;
		this.priority = NORMAL;
		this.modifiers = Needs.Modifiers.IS_NORMAL;
	}

	public Need(Consumer owner, float priority) {
		this(owner);
		this.priority = priority;
	}

	public Need(Consumer owner, float priority, Needs.Modifiers modifier) {
		this(owner);
		this.priority = priority;
		this.modifiers = modifier;
	}

	public Consumer getOwner() {
		return this.owner;
	}

	public boolean isSatisfied() {
		return isSatisfied;
	}

	public void setSatisfied(boolean satisfied) {
		this.isSatisfied = satisfied;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public float getPriority() {
		return this.priority;
	}

	public Needs.Modifiers getModifiers() {
		return modifiers;
	}

	public abstract Needs.Types[] getTypes();

	public abstract boolean canReceive(Object offer);

}
