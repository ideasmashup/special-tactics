package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ideasmashup.specialtactics.agents.Agent;

public class Agents {

	protected List<Agent> agents;

	protected static Agents instance = null;

	protected Agents() {
		agents = new ArrayList<Agent>();
	}

	public static Agents getInstance() {
		if (instance == null) {
			instance = new Agents();

			System.out.println("Agents initialized");
		}

		return instance;
	}

	public void add(Agent agent) {
		agents.add(agent);
	}

	public void remove(Agent agent) {
		agents.remove(agent);
	}

	public List<Agent> getList() {
		return Collections.unmodifiableList(agents);
	}
}
