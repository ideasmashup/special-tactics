package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.List;

import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.listeners.SupplyListener;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedUnit;
import org.ideasmashup.specialtactics.utils.Utils;

import bwapi.Unit;
import bwapi.UnitType;

public class Supplies implements Consumer {

	protected List<SupplyListener> listeners;

	// FIXME temporary hack to "reserve" resouces (must replace with async
	//       needs-filling API
	protected int lockedSupply = 0;

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

	public static void lockSupply(int amount, boolean lock) {
		if (lock) {
			instance.lockedSupply += amount;
			onSupplyChange(-1);
		}
		else {
			instance.lockedSupply -= amount;
			onSupplyChange(-1);
		}

		if (instance.lockedSupply < 0) {
			System.out.println("ERROR: locked negative supply!! "+ instance.lockedSupply);
		}
	}

	public static int getSupply() {
		return Utils.get().getPlayer().supplyTotal() - Utils.get().getPlayer().supplyUsed() - instance.lockedSupply;
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
		// FIXME implement listeners and locking mechanism like in Resources

		// automatically create supply

		// FIXME remember when supply is laready being produced so that other workers
		//       continue being used for mining and other activities
		if (getSupply() <= 3) {
			System.out.println("Supply running low, requested next worker for supply building");
			Needs.add(new NeedUnit(Units.Types.WORKERS.getUnitType(), 0), instance);
		}

		// call all listeners
		for (SupplyListener ls : instance.listeners) {
			ls.onSupplyChange(getSupply());
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

				// FIXME ugly workaround to prevent minerals and gas for the pylons/dpot
				//       to be eaten up by other minerals consumers
				Resources.lockMinerals(type.mineralPrice(), true);
				Resources.lockGas(type.gasPrice(), true);

				unit.build(SimCities.getLocationForStructure(type, unit), type);

				return true;
			}
		}
		return false;
	}
}
