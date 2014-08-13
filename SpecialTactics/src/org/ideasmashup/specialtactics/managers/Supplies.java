package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.List;

import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.listeners.SupplyListener;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedUnit;

import bwapi.Unit;
import bwapi.UnitType;

public class Supplies implements Consumer {

	protected List<SupplyListener> listeners;

	protected static Supplies instance = null;

	protected Supplies() {
		listeners = new ArrayList<SupplyListener>();
	}

	public static void init() {
		if (instance == null) {
			instance = new Supplies();

			System.out.println("Supplies initialized");
		}
	}

	public static void addListener(SupplyListener ls) {
		instance.listeners.add(ls);
	}

	public static void removeListener(SupplyListener ls) {
		instance.listeners.remove(ls);
	}

	public static void removeAllListeners() {
		instance.listeners.clear();
	}

	public static void onSupplyChange(int supply) {
		// automatically create supply
		if (supply <= 3) {
			Needs.add(new NeedUnit(Units.Types.WORKERS.getUnitType(), 0), instance);
		}

		// call all listeners
		for (SupplyListener ls : instance.listeners) {
			ls.onSupplyChange(supply);
		}
	}

	@Override
	public Need[] getNeeds(boolean returnAll) {
		return null;
	}

	@Override
	public boolean fillNeeds(Object offer) {
		if (offer instanceof Unit) {
			Unit unit = (Unit) offer;
			if (unit.getType().isWorker()) {
				System.out.println("Supplies just received new worker #"+ unit.getID() +"!");
				UnitType type = Units.Types.SUPPLY.getUnitType();
				unit.build(SimCities.getLocationForStructure(type, unit), type);
				return true;
			}
		}
		return false;
	}
}
