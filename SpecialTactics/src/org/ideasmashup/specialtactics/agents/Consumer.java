package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.Needs;

public interface Consumer {

	// TODO provide reference to global needs manager
	// public void plugNeeds(Needs manager);

	// TODO initialize needee's own needs
	// public void initNeeds();

	// fetch needee's own needs (either all or only unsatisfied ones)
	public Need[] getNeeds(boolean returnAll);

	// attemp to fill needee's needs
	public boolean fillNeeds(Object offer);
}
