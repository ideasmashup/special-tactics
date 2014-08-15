package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ideasmashup.specialtactics.agents.Agent;

public class Agents {

	// low-level agents
	protected List<Agent> agents;

	// FIXME replace by real "singleton" pattern when debugging over!
	protected static Agents instance = null;

	protected Agents() {
		agents = new ArrayList<Agent>();
	}

	public static void init() {
		if (instance == null) {
			instance = new Agents();

			System.out.println("Agents initialized");
		}
	}

	public static void add(Agent agent) {
		instance.agents.add(agent);
	}

	public static void remove(Agent agent) {
		instance.agents.remove(agent);
	}

	public static List<Agent> getList() {
		return Collections.unmodifiableList(instance.agents);
	}
}
