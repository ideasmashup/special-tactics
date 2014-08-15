package org.ideasmashup.specialtactics.managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.ideasmashup.specialtactics.agents.UnitAgent;
import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.listeners.ResourcesListener;
import org.ideasmashup.specialtactics.listeners.SupplyListener;
import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.needs.Need;

import bwapi.Unit;

/**
 * <h3>Global needs manager.</h3>
 * <p>
 * All needs get pushed here by consumers needing them. The manager watches
 * events such as ressources and units changes.
 * </p>
 * <p>
 * Whenever a change occur the manaer will lookup agents that may be satisfied
 * by that change :
 * </p>
 * <ul>
 * <li>a new unit for a UnitConsumer</li>
 * <li>a new ressource for a RessourceConsumer</li>
 * <li>a new supply for a SupplyConsumer</li>
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
 * has to call again {@link #add(Need, UnitAgent)}.
 * </p>
 *
 * @author William ANGER
 *
 */
public class Needs implements UnitListener, ResourcesListener, SupplyListener {

	protected Map<Types, LinkedList<Need>> needs;
	protected Map<Need, Consumer> consumers;

	// FIXME replace by real "singleton" pattern when debugging over!
	protected static Needs instance = null;

	protected Needs() {
		// init local fields
		needs = new HashMap<Types, LinkedList<Need>>();

		needs.put(Types.RESOURCES, new LinkedList<Need>());
		needs.put(Types.UNIT, new LinkedList<Need>());

		consumers = new HashMap<Need, Consumer>();
	}

	public static Needs getInstance() {
		if (instance == null) {
			instance = new Needs();

			// bind itself to units events, supply and resources events
			Units.getInstance().addListener(instance);
			Resources.getInstance().addListener(instance);
			Supplies.getInstance().addListener(instance);

			System.out.println("Needs initialized");
		}

		return instance;
	}

	public void add(Need need, Consumer owner) {
		consumers.put(need, owner);

		for (Types type : need.getTypes()) {
			System.out.println("  - added "+ type.name() +" NEED for "+ owner.toString() +" (p="+ need.getPriority() +")");

			// sorted insert based on priority (highest is last)
			LinkedList<Need> llist = needs.get(type);

			if (llist.size() == 0) {
				llist.add(need);
			}
			else if (need.getPriority() == Need.CRITICAL
					|| llist.get(0).getPriority() > need.getPriority()) {
				// first place for CRITICAL or need with smallest value
				llist.add(0, need);
			}
			else if (need.getPriority() == Need.USELESS
					|| llist.get(llist.size() - 1).getPriority() < need
					.getPriority()) {
				// last place for USELESS or need with biggest value
				llist.add(llist.size(), need);
			}
			else {
				int i = 0;
				while (llist.get(i).getPriority() < need.getPriority()) {
					i++;
				}
				llist.add(i, need);
			}
		}
	}

	public void remove(Need need) {
		Consumer owner = consumers.remove(need);

		for (Types type : need.getTypes()) {
			System.out.println("  - removed "+ type.name() +" NEED for "+ owner.toString() +" (p="+ need.getPriority() +")");
			needs.get(type).remove(need);
		}
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
		System.out.println("Needs.get(UNIT).size() = "+ needs.get(Types.UNIT).size());

		for (Need need : needs.get(Types.UNIT)) {
			if (need.canReceive(unit)) {
				Consumer consumer = consumers.get(need);
				if (consumer.fillNeeds(unit)) {
					System.out.println("  - consumer satisfied !");
					// this unit has been overtaken by the unit consumer
					remove(need);
					break;
				}
			}
		}
	}

	@Override
	public void onResourcesChange(int minerals, int gas) {
		// assign new unit to needees
		//System.out.println("Needs.onRessourcesChange()");
		//System.out.println("Needs.get(RESOURCES).size() = "+ needs.get(Types.RESOURCES).size());

		for (Need need : needs.get(Types.RESOURCES)) {
			Consumer consumer = consumers.get(need);
			if (consumer.fillNeeds(null)) {
				System.out.println("  - ressources consumer satisfied !");
				remove(need);
				break;
			}
		}

	}

	@Override
	public void onSupplyChange(int supply) {
		System.out.println("Needs.onSupplyChange()");
		System.out.println("Needs.get(SUPPLY).size() = "+ needs.get(Types.SUPPLY).size());

		for (Need need : needs.get(Types.SUPPLY)) {
			Consumer consumer = consumers.get(need);
			if (consumer.fillNeeds(null)) {
				System.out.println("  - supply consumer satisfied !");
				remove(need);
				break;
			}
		}
	}

	public static enum Types {
		UNIT,
		RESOURCES,
		SUPPLY,
		/*
		DEFENSE
		ATTACK
		DROP // maybe a different kind of ATTACK?
		REPAIRS //
		MAPCONTROL
		VISION
		DETECTION // maybe a kind of VISION?
		*/
	}
}
