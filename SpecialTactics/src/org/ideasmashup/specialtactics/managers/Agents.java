package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ideasmashup.specialtactics.agents.Agent;

public class Agents {

	protected List<Agent> agents;

	protected static Agents instance = null;

	protected Agents() {
		agents = Collections.synchronizedList(new ArrayList<Agent>());
	}

	public static Agents getInstance() {
		if (instance == null) {
			instance = new Agents();

			System.out.println("Agents initialized");
		}

		return instance;
	}

	public int getAgentsCount() {
		synchronized(agents) {
			return agents.size();
		}
	}

	public boolean contains(Agent agent) {
		synchronized(agents) {
			return agents.contains(agent);
		}
	}

	public void add(Agent agent) {
		System.out.println("Agents : added "+ agent);
		synchronized(agents) {
			agents.add(agent);
		}
	}

	public void remove(Agent agent) {
		System.out.println("Agents : removed "+ agent);
		synchronized(agents) {
			agents.remove(agent);
		}
	}

	public List<Agent> getList() {
		synchronized(agents) {
			return Collections.synchronizedList(agents);
		}
	}
}
