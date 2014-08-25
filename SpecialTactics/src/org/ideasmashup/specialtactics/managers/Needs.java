package org.ideasmashup.specialtactics.managers;

import java.util.LinkedList;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.agents.Consumer;
import org.ideasmashup.specialtactics.agents.Producer;
import org.ideasmashup.specialtactics.agents.UnitAgent;
import org.ideasmashup.specialtactics.listeners.ResourcesListener;
import org.ideasmashup.specialtactics.listeners.SupplyListener;
import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.managers.Units.Filter;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedResources;
import org.ideasmashup.specialtactics.needs.NeedSupply;
import org.ideasmashup.specialtactics.needs.NeedUnit;

import bwapi.Unit;
import bwapi.UnitType;

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

	protected LinkedList<Need> nUnits;
	protected LinkedList<Need> nResources;
	protected LinkedList<Need> nSupplies;

	protected LinkedList<Producer> producers;

	protected static Needs instance = null;

	protected Needs() {
		// init local fields
		this.nUnits = new LinkedList<Need>();
		this.nResources = new LinkedList<Need>();
		this.nSupplies = new LinkedList<Need>();

		this.producers = new LinkedList<Producer>();
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

	protected void sortedInsert(LinkedList<Need> llist, Need need) {
		// sorted insert based on priority (highest is last)

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

	public void add(Need need) {
		if (need instanceof NeedUnit) {
			sortedInsert(this.nUnits, need);
			System.out.println("  - added NeedUnit for "+ need.getOwner().toString());
			System.out.println("  - total NeedUnit = "+ nUnits.size());
		}
		else if (need instanceof NeedResources) {
			sortedInsert(this.nResources, need);
			System.out.println("  - added NeedResources for "+ need.getOwner().toString());
			System.out.println("  - total NeedResources = "+ nResources.size());
		}
		else if (need instanceof NeedSupply) {
			sortedInsert(this.nSupplies, need);
			System.out.println("  - added NeedSupply for "+ need.getOwner().toString());
			System.out.println("  - total NeedSupply now = "+ nSupplies.size());
		}
		else {
			System.err.println("  - not added because of unknown Need type : "+ need);
		}
	}

	public void remove(Need need) {
		if (need instanceof NeedUnit) {
			this.nUnits.remove(need);
			System.out.println("  - removed NeedUnit for "+ need.getOwner().toString());
			System.out.println("  - total NeedUnit now = "+ nUnits.size());
		}
		else if (need instanceof NeedResources) {
			this.nResources.remove(need);
			System.out.println("  - removed NeedResources for "+ need.getOwner().toString());
			System.out.println("  - total NeedResources now = "+ nResources.size());
		}
		else if (need instanceof NeedSupply) {
			this.nSupplies.remove(need);
			System.out.println("  - removed NeedSupply for "+ need.getOwner().toString());
			System.out.println("  - total NeedSupply now = "+ nSupplies.size());
		}
		else {
			System.err.println("  - not removed because of unknown Need type : "+ need);
		}
	}

	public void add(Producer producer) {
		producers.add(producer);
	}

	public void remove(Producer producer) {
		producers.remove(producer);
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
		//
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
		//System.out.println("Needs.onUnitComplete()");
		//System.out.println("Needs.get(UNIT).size() = "+ needs.get(Types.UNIT).size());

		for (Need need : nUnits) {
			if (need.canReceive(unit)) {
				Consumer consumer = need.getOwner();
				if (consumer.fillNeeds(unit)) {
					System.out.println("  - consumer satisfied !");

					if (need.getModifiers() == Needs.Modifiers.IS_NORMAL) {
						// when normal needs are satified they are removed from the
						// global stack immediately
						remove(need);
						break;
					}
					else if (need.getModifiers() == Needs.Modifiers.IS_TRANSIENT) {
						// when transient needs are satified they are removed but
						// their offer can still be passed to satifsy the next consumer
						remove(need);
						continue;
					}
					else if (need.getModifiers() == Needs.Modifiers.IS_PERMANENT) {
						// when permanent needs are satified they are removed but
						// added back immediately because they can't be satisfied

						// Yet the need often changes when fillNeed() is called
						// so when it's added back it may have different priorities
						// modifiers, etc

						remove(need);
						add(need);
						break;
					}
					break;
				}
			}
		}

		if (nUnits.size() == 0) {
			// no more needs...
			AI.say("All mineral patchs say they are satisfied...");
		}
	}

	@Override
	public void onResourcesChange(int minerals, int gas) {
		//System.out.println("Needs.onRessourcesChange()");
		//System.out.println("Needs.get(RESOURCES).size() = "+ needs.get(Types.RESOURCES).size());

		for (Need need : nResources) {
			Consumer consumer = need.getOwner();
			if (consumer.fillNeeds(null)) {
				System.out.println("  - ressources consumer satisfied !");

				if (need.getModifiers() == Needs.Modifiers.IS_NORMAL) {
					// when normal needs are satified they are removed from the
					// global stack immediately
					remove(need);
					break;
				}
				else if (need.getModifiers() == Needs.Modifiers.IS_TRANSIENT) {
					// when transient needs are satified they are removed but
					// their offer can still be passed to satifsy the next consumer
					remove(need);
					continue;
				}
				else if (need.getModifiers() == Needs.Modifiers.IS_PERMANENT) {
					// when permanent needs are satified they are removed but
					// added back immediately because they can't be satisfied

					// Yet the need often changes when fillNeed() is called
					// so when it's added back it may have different priorities
					// modifiers, etc

					remove(need);
					add(need);
					break;
				}
				break;
			}
		}

	}

	@Override
	public void onSupplyChange(int supply) {
		//System.out.println("Needs.onSupplyChange()");
		//System.out.println("Needs.get(SUPPLY).size() = "+ needs.get(Types.SUPPLY).size());

		for (Need need : nSupplies) {
			Consumer consumer = need.getOwner();
			if (consumer.fillNeeds(null)) {
				System.out.println("  - supply consumer satisfied !");

				if (need.getModifiers() == Needs.Modifiers.IS_NORMAL) {
					// when normal needs are satified they are removed from the
					// global stack immediately
					remove(need);
					break;
				}
				else if (need.getModifiers() == Needs.Modifiers.IS_TRANSIENT) {
					// when transient needs are satified they are removed but
					// their offer can still be passed to satifsy the next consumer
					remove(need);
					continue;
				}
				else if (need.getModifiers() == Needs.Modifiers.IS_PERMANENT) {
					// when permanent needs are satified they are removed but
					// added back immediately because they can't be satisfied

					// Yet the need often changes when fillNeed() is called
					// so when it's added back it may have different priorities
					// modifiers, etc

					remove(need);
					add(need);
					break;
				}
			}
		}
	}

	protected Filter filter = new Filter() {
		@Override
		public boolean allow(Unit unit) {
			// needs manager listens to all units events so allow all
			return true;
		};
	};

	@Override
	public Filter getFilter() {
		return this.filter;
	}

	@Deprecated
	public static enum Types {// n instanceof NeedUnit | NeedResources...
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

	public static enum Modifiers{
		IS_TRANSIENT, // successful offers to transients needs must be offered to next in line too
		IS_NORMAL,    // successful offers to normal needs will consume the "filled" need object
		IS_PERMANENT, // successful offers to permanent needs won't consume the "filled" need
	}
}
