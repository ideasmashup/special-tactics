package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.List;

import org.ideasmashup.specialtactics.listeners.SupplyListener;

public class Supplies {
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
		// call all listeners
		for (SupplyListener ls : instance.listeners) {
			ls.onSupplyChange(supply);
		}
	}
}
