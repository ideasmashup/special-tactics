package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.listeners.ResourcesListener;

public class Resources {

	protected List<ResourcesListener> listeners;

	// FIXME temporary hack to "reserve" resouces (must replace with async
	//       needs-filling API

	// ----
	protected Map<Consumer, Integer> reservedMinerals;
	protected int reservedMineralsTotal;

	protected Map<Consumer, Integer> reservedGas;
	protected int reservedGasTotal;
	// ------

	protected static Resources instance = null;

	protected Resources() {
		listeners = new ArrayList<ResourcesListener>();

		reservedMinerals = new HashMap<Consumer, Integer>();
		reservedMineralsTotal = 0;

		reservedGas = new HashMap<Consumer, Integer>();
		reservedGasTotal = 0;
	}

	public static Resources getInstance() {
		if (instance == null) {
			instance = new Resources();

			System.out.println("Resources initialized");
		}

		return instance;
	}

	public void reserveMinerals(int amount, Consumer owner) {
		if (!reservedMinerals.containsKey(owner)) {
			System.out.println("Resources reserved "+ amount +" minerals for "+ owner);
			reservedMinerals.put(owner, amount);

			Collection<Integer> rs = reservedMinerals.values();
			int total = 0;
			for (Integer r : rs) {
				total += r;
			}
			this.reservedMineralsTotal = total;

			onResourcesChange(-1, -1);
		}
		else {
			System.err.println("Cannot add more minerals to already reserved by "+ owner);
		}
	}

	public void reserveGas(int amount, Consumer owner) {
		if (!reservedGas.containsKey(owner)) {
			System.out.println("Resources reserved "+ amount +" gas for "+ owner);
			reservedGas.put(owner, amount);

			Collection<Integer> rs = reservedGas.values();
			int total = 0;
			for (Integer r : rs) {
				total += r;
			}
			this.reservedGasTotal = total;

			onResourcesChange(-1, -1);
		}
		else {
			System.err.println("Cannot add more minerals to already reserved by "+ owner);
		}
	}

	public void unreserve(Consumer owner) {
		System.out.println("Resources unreserved all resources allocated to "+ owner);
		reservedMinerals.remove(owner);
		reservedGas.remove(owner);
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
		return AI.getPlayer().minerals() - reservedMineralsTotal
			+ reservedMinerals.get(owner);
	}

	public int getGas() {
		// return "public" gas quantity (total available minus reserved)
		return AI.getPlayer().gas() - reservedGasTotal;
	}

	public int getGas(Consumer owner) {
		// return "private" gas + public gas (see: getMinerals(Consumer))

		// TODO maybe optimize this so that only the first in line can access
		//      his reserved amount asap?
		return AI.getPlayer().gas() - reservedGasTotal
			+ reservedGas.get(owner);
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
