package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.agents.Agent;
import org.ideasmashup.specialtactics.listeners.ResourcesListener;

public class Resources {

	protected List<ResourcesListener> listeners;

	// FIXME temporary hack to "reserve" resouces (must replace with async
	//       needs-filling API

	// ----
	protected Map<Object, Integer> reservedMinerals;
	protected int reservedMineralsTotal;

	protected Map<Object, Integer> reservedGas;
	protected int reservedGasTotal;
	// ------

	protected static Resources instance = null;

	protected Resources() {
		listeners = new ArrayList<ResourcesListener>();

		reservedMinerals = new HashMap<Object, Integer>();
		reservedMineralsTotal = 0;

		reservedGas = new HashMap<Object, Integer>();
		reservedGasTotal = 0;
	}

	public static Resources getInstance() {
		if (instance == null) {
			instance = new Resources();

			System.out.println("Resources initialized");
		}

		return instance;
	}

	public void reserveMinerals(int amount, Agent owner) {
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

	public void reserveGas(int amount, Object owner) {
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

	public void unreserve(Object owner) {
		System.out.println("Resources unreserved all resources allocated to "+ owner);
		reservedMinerals.remove(owner);
		reservedGas.remove(owner);
	}

	public int getMinerals() {
		return AI.getPlayer().minerals() - reservedMineralsTotal;
	}

	public int getGas() {
		return AI.getPlayer().gas() - reservedGasTotal;
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
