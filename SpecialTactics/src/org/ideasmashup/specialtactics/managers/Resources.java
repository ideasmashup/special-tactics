package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.List;

import org.ideasmashup.specialtactics.listeners.ResourcesListener;

public class Resources {

	protected List<ResourcesListener> listeners;

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
			ls.onResourcesChange(minerals, gas);;
		}
	}

}
