package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.List;

import org.ideasmashup.specialtactics.listeners.ResourcesListener;
import org.ideasmashup.specialtactics.utils.Utils;

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

	public static void init() {
		if (instance == null) {
			instance = new Resources();

			System.out.println("Resources initialized");
		}
	}

	public static void lockMinerals(int amount, boolean lock) {
		if (lock) {
			System.out.println("Resources reserved "+ amount +" minerals");
			instance.lockedMinerals += amount;
			onResourcesChange(-1, -1);
		}
		else {
			System.out.println("Resources unreserved "+ amount +" minerals");
			instance.lockedMinerals -= amount;
			onResourcesChange(-1, -1);
		}

		if (instance.lockedMinerals < 0) {
			System.out.println("ERROR: locked negative minerals!! "+ instance.lockedMinerals);
		}
	}

	public static void lockGas(int amount, boolean lock) {
		if (lock) {
			System.out.println("Resources reserved "+ amount +" gas");
			instance.lockedGas += amount;
			onResourcesChange(-1, -1);
		}
		else {
			System.out.println("Resources unreserved "+ amount +" gas");
			instance.lockedGas -= amount;
			onResourcesChange(-1, -1);
		}

		if (instance.lockedGas < 0) {
			System.out.println("ERROR: locked negative gas!! "+ instance.lockedGas);
		}
	}

	public static int getMinerals() {
		return Utils.get().getPlayer().minerals() - instance.lockedMinerals;
	}

	public static int getGas() {
		return Utils.get().getPlayer().gas() - instance.lockedGas;
	}

	public static void addListener(ResourcesListener ls) {
		instance.listeners.add(ls);
	}

	public static void removeListener(ResourcesListener ls) {
		instance.listeners.remove(ls);
	}

	public static void removeAllListeners() {
		instance.listeners.clear();
	}

	public static void onResourcesChange(int minerals, int gas) {
		// call all listeners
		for (ResourcesListener ls : instance.listeners) {
			ls.onResourcesChange(getMinerals(), getGas());
		}
	}

}
