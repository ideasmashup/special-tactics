package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.listeners.ResourcesListener;

public class Resources {

	protected List<ResourcesListener> listeners;

	protected Map<Consumer, Integer> reservedMinerals;
	protected int reservedMineralsTotal;
	protected Map<Consumer, Integer> reservedGas;
	protected int reservedGasTotal;
	protected LinkedList<Consumer> consumers;

	protected static Resources instance = null;

	protected Resources() {
		listeners = new ArrayList<ResourcesListener>();

		reservedMinerals = new HashMap<Consumer, Integer>();
		reservedMineralsTotal = 0;

		reservedGas = new HashMap<Consumer, Integer>();
		reservedGasTotal = 0;

		// list of consumers because map isn't ordered
		consumers = new LinkedList<Consumer>();
	}

	public static Resources getInstance() {
		if (instance == null) {
			instance = new Resources();

			System.out.println("Resources initialized");
		}

		return instance;
	}

	public boolean hasReserved(Consumer owner) {
		return reservedMinerals.containsKey(owner) || reservedGas.containsKey(owner);
	}

	protected void updateReservedMineralsTotal() {
		Collection<Integer> rs = reservedMinerals.values();
		int total = 0;
		for (Integer r : rs) {
			total += r;
		}
		this.reservedMineralsTotal = total;
	}

	public void reserveMinerals(int amount, Consumer owner) {
		if (!reservedMinerals.containsKey(owner)) {
			System.out.println("Resources reserved "+ amount +" minerals for "+ owner);
			reservedMinerals.put(owner, amount);

			updateReservedMineralsTotal();

			if (!this.consumers.contains(owner)) {
				this.consumers.addFirst(owner);
			}

			onResourcesChange(getMinerals(), getGas());
		}
		else {
			System.err.println("Cannot add more minerals to already reserved by "+ owner);
		}
	}

	protected void updateReservedGasTotal() {
		Collection<Integer> rs = reservedGas.values();
		int total = 0;
		for (Integer r : rs) {
			total += r;
		}
		this.reservedGasTotal = total;
	}

	public void reserveGas(int amount, Consumer owner) {
		if (!reservedGas.containsKey(owner)) {
			System.out.println("Resources reserved "+ amount +" gas for "+ owner);
			reservedGas.put(owner, amount);

			updateReservedGasTotal();

			if (!this.consumers.contains(owner)) {
				this.consumers.addFirst(owner);
			}

			onResourcesChange(getMinerals(), getGas());
		}
		else {
			System.err.println("Cannot add more minerals to already reserved by "+ owner);
		}
	}

	public void unreserve(Consumer owner) {
		System.out.println("Resources unreserved all resources allocated to "+ owner);

		this.consumers.remove(owner);
		this.reservedMinerals.remove(owner);
		this.reservedGas.remove(owner);

		updateReservedMineralsTotal();
		updateReservedGasTotal();

		onResourcesChange(getMinerals(), getGas());
	}

	public int getMinerals() {
		// return "public" minerals (ones that haven't been reserved yet)
		return AI.getPlayer().minerals() - reservedMineralsTotal;
	}

	public int getMinerals(Consumer owner) {
		// return "private" minerals (public ones + ones that this owner may
		// have in his "reserved" slot

		// TODO maybe optimize this so that only the first in line can access
		//      his reserved amount asap?
		int minerals =  AI.getPlayer().minerals() - reservedMineralsTotal;

		if (reservedMinerals.containsKey(owner) && consumers.getFirst() == owner) {
			minerals += reservedMinerals.get(owner);
		}

		return minerals;
	}

	public int getGas() {
		// return "public" gas quantity (total available minus reserved)
		return AI.getPlayer().gas() - reservedGasTotal;
	}

	public int getGas(Consumer owner) {
		// return "private" gas + public gas (see: getMinerals(Consumer))

		// TODO maybe optimize this so that only the first in line can access
		//      his reserved amount asap?
		int gas = AI.getPlayer().gas() - reservedGasTotal;

		if (reservedGas.containsKey(owner) && consumers.getFirst() == owner) {
			gas += reservedGas.get(owner);
		}

		return gas;
	}

	public void addListener(ResourcesListener ls) {
		listeners.add(ls);
	}

	public void removeListener(ResourcesListener ls) {
		listeners.remove(ls);
	}

	public void removeAllListeners() {
		listeners.clear();
	}

	public void onResourcesChange(int minerals, int gas) {
		// call all listeners
		for (ResourcesListener ls : listeners) {
			ls.onResourcesChange(getMinerals(), getGas());
		}
	}

}
