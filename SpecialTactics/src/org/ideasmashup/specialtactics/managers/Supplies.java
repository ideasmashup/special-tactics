package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.agents.Agent;
import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.agents.MakeSupply;
import org.ideasmashup.specialtactics.listeners.SupplyListener;

public class Supplies {

	protected static Supplies instance = null;

	protected List<SupplyListener> listeners;
	protected List<Agent> suppliers;

	protected Map<Consumer, Integer> reservedSupply;
	protected int reservedSupplyTotal;

	protected LinkedList<Consumer> consumers;

	protected Supplies() {
		listeners = new ArrayList<SupplyListener>();
		suppliers = new ArrayList<Agent>();

		reservedSupply = new HashMap<Consumer, Integer>();
		reservedSupplyTotal = 0;

		// list of consumers because map isn't ordered
		consumers = new LinkedList<Consumer>();
	}

	public static Supplies getInstance() {
		if (instance == null) {
			instance = new Supplies();

			System.out.println("Supplies initialized");
		}

		return instance;
	}

	public boolean hasReserved(Consumer owner) {
		return reservedSupply.containsKey(owner);
	}

	protected void updateReservedSupply() {
		Collection<Integer> rs = reservedSupply.values();
		int total = 0;
		for (Integer r : rs) {
			total += r;
		}
		this.reservedSupplyTotal = total;
	}

	public void reserveSupply(int amount, Consumer owner) {
		if (!reservedSupply.containsKey(owner)) {
			System.out.println("Supplies reserved "+ amount +" supply for "+ owner);
			reservedSupply.put(owner, amount);

			updateReservedSupply();

			if (!this.consumers.contains(owner) && consumers.getFirst() == owner) {
				this.consumers.addFirst(owner);
			}

			onSupplyChange(getSupply());
		}
		else {
			System.err.println("Cannot add more minerals to already reserved by "+ owner);
		}
	}

	public void unreserveSupply(Consumer owner) {
		System.out.println("Supplies unreserved allocated to "+ owner);
		this.consumers.remove(owner);
		this.reservedSupply.remove(owner);

		updateReservedSupply();

		onSupplyChange(getSupply());
	}

	public int getSupply() {
		// public supplies
		return AI.getPlayer().supplyTotal() - AI.getPlayer().supplyUsed() - reservedSupplyTotal;
	}

	public int getSupply(Consumer owner) {
		// public supplies + private supplies for this consumer
		int supply = AI.getPlayer().supplyTotal() - AI.getPlayer().supplyUsed() - reservedSupplyTotal;

		if (reservedSupply.containsKey(owner)) {
			supply += reservedSupply.get(owner);
		}

		return supply;
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

		if (AI.getPlayer().supplyTotal() - AI.getPlayer().supplyUsed() <= Units.Types.WORKERS.getUnitType().supplyRequired() * 2
			&& suppliers.isEmpty()) {
			// "real" supply running low, must create a new "supply provider" (e.g. supplier)
			// unless there are already suppliers in action?

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
				// generic cross-race agent that creates a supply unit
				suppliers.add(new MakeSupply());
			}
		}

		// check reserved resources to notify their owners
		Consumer first = this.consumers.peekFirst();

		if (reservedSupply.get(first) >= getSupply(first)) {
			// the first consumer can be satisfied
			if (first.fillNeeds(null)) {
				// consumer satisfied so skip other listeners because minerals
				// have been consumed so no need to do more in this frame
				return;
			}
		}

		// call all listeners
		for (SupplyListener ls : listeners) {
			ls.onSupplyChange(getSupply());
		}
	}
}
