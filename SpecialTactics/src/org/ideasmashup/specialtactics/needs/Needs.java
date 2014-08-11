package org.ideasmashup.specialtactics.needs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ideasmashup.specialtactics.agents.Agent;
import org.ideasmashup.specialtactics.agents.UnitConsumer;
import org.ideasmashup.specialtactics.brains.Units;
import org.ideasmashup.specialtactics.utils.UnitListener;

import bwapi.Unit;

/**
 * <h3>Global needs manager.</h3>
 * <p>
 * All needs get pushed here by agents who are needing them. The manager watches
 * events such as ressources and units changes.
 * </p>
 * <p>
 * Whenever a change occur the manaer will lookup agents that may be satisfied
 * by that change :
 * </p>
 * <ul>
 * <li>a new unit for a UnitConsumer</li>
 * <li>a new ressource for a RessourceConsumer</li>
 * <li>a new threat for a ThreatConsumer</li>
 * <li>etc</li>
 * </ul>
 * <p>
 * The agents will then receive an "offer". For example a new SCV is created,
 * the manager looks for UnitConsumers that want an SCV (example a mineral patch)
 * then calls <pre>boolean satisfied = consumer.fillNeed(scv);</pre>
 * </p>
 * <p>
 * If the consumer (agent) is satisfied then the need is removed from the global
 * list of needs. Whenever that agent or another one has that need again it just
 * has to call again {@link #add(Need, Agent)}.
 * </p>
 *
 * @author William ANGER
 *
 */
public class Needs implements UnitListener{

	protected List<Need> needs;
	protected Map<Need, Agent> agents;

	protected static Needs instance = null;

	protected Needs() {
		// init local fields
		needs = new ArrayList<Need>();
		agents = new HashMap<Need, Agent>();
	}

	public static void init() {
		if (instance == null) {
			instance = new Needs();

			// bind itself to units events
			Units.addListener(instance);

			System.out.println("Needs initialized");
		}
	}

	public static void add(Need need, Agent owner) {
		System.out.println("  - added need (priority = "+ need.getPriority() +") for "+ owner.toString());

		instance.needs.add(need);
		instance.agents.put(need, owner);
	}

	public static void remove(Need need) {
		instance.needs.remove(need);
		Agent owner = instance.agents.remove(need);

		System.out.println("  - removed need (priority = "+ need.getPriority() +") for "+ owner.toString());
	}

	@Override
	public void onUnitDiscover(Unit unit) {
		//
	}

	@Override
	public void onUnitEvade(Unit unit) {
		//
	}

	@Override
	public void onUnitShow(Unit unit) {
		//
	}

	@Override
	public void onUnitHide(Unit unit) {
		//
	}

	@Override
	public void onUnitCreate(Unit unit) {
		System.out.println("Needs.onUnitCreate()");
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		//

	}

	@Override
	public void onUnitMorph(Unit unit) {
		//
	}

	@Override
	public void onUnitRenegade(Unit unit) {
		//
	}

	@Override
	public void onUnitComplete(Unit unit) {
		// assign new unit to needees
		System.out.println("Needs.onUnitComplete()");
		System.out.println("Needs.size() = "+ needs.size());

		for (Need need : needs) {
			if (need instanceof NeedUnit) {
				NeedUnit ns = (NeedUnit) need;
				if (ns.type == unit.getType()) {
					// this need can be filled by this new unit
					UnitConsumer agent = (UnitConsumer) agents.get(need);
					if (agent.fillNeed(unit)) {
						System.out.println("  - consumer satisfied !");
						// this unit has been overtaken by the unit consumer
						remove(need);
						break;
					}
				}
			}
		}
	}


}
