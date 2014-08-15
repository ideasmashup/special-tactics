package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.List;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.listeners.ResourcesListener;

public class Resources {

	protected List<ResourcesListener> listeners;

	// FIXME temporary hack to "reserve" resouces (must replace with async
	//       needs-filling API
	protected int lockedMinerals = 0;
	protected int lockedGas = 0;

	protected static Resources instance = null;

	protected Resources() {
		listeners = new ArrayList<ResourcesListener>();
	}

	public static Resources getInstance() {
		if (instance == null) {
			instance = new Resources();

			System.out.println("Resources initialized");
		}

		return instance;
	}

	public void lockMinerals(int amount, boolean lock) {
		if (lock) {
			System.out.println("Resources reserved "+ amount +" minerals");
			lockedMinerals += amount;
			onResourcesChange(-1, -1);
		}
		else {
			System.out.println("Resources unreserved "+ amount +" minerals");
			lockedMinerals -= amount;
			onResourcesChange(-1, -1);
		}

		if (lockedMinerals < 0) {
			System.err.println("ERROR: locked negative minerals!! "+ lockedMinerals);
		}
	}

	public void lockGas(int amount, boolean lock) {
		if (lock) {
			System.out.println("Resources reserved "+ amount +" gas");
			lockedGas += amount;
			onResourcesChange(-1, -1);
		}
		else {
			System.out.println("Resources unreserved "+ amount +" gas");
			lockedGas -= amount;
			onResourcesChange(-1, -1);
		}

		if (lockedGas < 0) {
			System.err.println("ERROR: locked negative gas!! "+ lockedGas);
		}
	}

	public int getMinerals() {
		return AI.getPlayer().minerals() - lockedMinerals;
	}

	public int getGas() {
		return AI.getPlayer().gas() - lockedGas;
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
