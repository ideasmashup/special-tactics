package org.ideasmashup.specialtactics.agents;

import java.util.List;

import org.ideasmashup.specialtactics.needs.Need;

import bwapi.Unit;

public interface UnitConsumer {

	public abstract List<Need> getNeeds();

	public abstract boolean fillNeed(Unit offer);

}