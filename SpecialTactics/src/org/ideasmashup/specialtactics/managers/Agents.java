package org.ideasmashup.specialtactics.managers;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ideasmashup.specialtactics.agents.Agent;

public class Agents {

	protected List<Agent> agents;

	protected static Agents instance = null;

	protected Agents() {
		agents = new CopyOnWriteArrayList<Agent>(); // concurrent
	}

	public static Agents getInstance() {
		if (instance == null) {
			instance = new Agents();

			System.out.println("Agents initialized");
		}

		return instance;
	}

	public int getAgentsCount() {
		return agents.size();
	}

	public boolean contains(Agent agent) {
		return agents.contains(agent);
	}

	public void add(Agent agent) {
		System.out.println("Agents : added "+ agent);
		agents.add(agent);
	}

	public void remove(Agent agent) {
		System.out.println("Agents : removed "+ agent);
		agents.remove(agent);
	}

	public List<Agent> getList() {
		return Collections.unmodifiableList(agents);
	}
}
