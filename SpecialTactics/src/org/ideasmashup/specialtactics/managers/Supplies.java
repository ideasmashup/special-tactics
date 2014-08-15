package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.agents.Agent;
import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.agents.MakeSupply;
import org.ideasmashup.specialtactics.listeners.SupplyListener;
import org.ideasmashup.specialtactics.needs.Need;

public class Supplies implements Consumer {

	protected static Supplies instance = null;

	protected List<SupplyListener> listeners;
	protected List<Agent> suppliers;

	// FIXME temporary hack to "reserve" resouces (must replace with async
	//       needs-filling API
	protected int lockedSupply = 0;

	protected Supplies() {
		listeners = new ArrayList<SupplyListener>();
		suppliers = new ArrayList<Agent>();
	}

	public static Supplies getInstance() {
		if (instance == null) {
			instance = new Supplies();

			System.out.println("Supplies initialized");
		}

		return instance;
	}

	public void lockSupply(int amount, boolean lock) {
		if (lock) {
			lockedSupply += amount;
			onSupplyChange(-1);
		}
		else {
			lockedSupply -= amount;
			onSupplyChange(-1);
		}

		if (lockedSupply < 0) {
			System.err.println("ERROR: locked negative supply!! "+ lockedSupply);
		}
	}

	public int getSupply() {
		return AI.getPlayer().supplyTotal() - AI.getPlayer().supplyUsed() - lockedSupply;
	}

	public void addListener(SupplyListener ls) {
		listeners.add(ls);
	}

	public void removeListener(SupplyListener ls) {
		listeners.remove(ls);
	}

	public void removeAllListeners() {
		listeners.clear();
	}

	public void onSupplyChange(int supply) {
		// automatically create supply

		// FIXME remember when supply is laready being produced so that other workers
		//       continue being used for mining and other activities

		if (getSupply() <= Units.Types.WORKERS.getUnitType().supplyRequired() * 3) {
			// supply running low, must create a new "supply provider" (e.g. supplier)
			// unless there are already suppliers in action

			// kill all suppliers that are inactive
			List<Agent> zombies = new LinkedList<Agent>();
			for (Agent supplier : suppliers) {
				if (supplier.isDestroyed()) {
					zombies.add(supplier);
				}
			}
			for (Agent zombie : zombies) {
				suppliers.remove(zombie);
			}

			// no suppliers left alive, need to create a new one
			if (suppliers.isEmpty()) {
				suppliers.add(new MakeSupply());
			}
		}

		// call all listeners
		for (SupplyListener ls : listeners) {
			ls.onSupplyChange(getSupply());
		}
	}

	@Override
	public Need[] getNeeds(boolean returnAll) {
		return null;
	}

	@Override
	public boolean fillNeeds(Object offer) {
		return false;
	}
}
