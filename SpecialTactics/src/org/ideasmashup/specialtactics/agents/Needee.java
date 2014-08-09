package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.Needs;

public interface Needee {

	// provide reference to global needs manager
	public void plugNeeds(Needs needs);

	// initialize needee's own needs
	public void initNeeds();

	// fetch needee's own needs
	public Need[] getNeeds();

	// attemp to fill needee's needs
	public boolean fillNeeds(Object proposal);
}
