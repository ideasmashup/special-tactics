package org.ideasmashup.specialtactics.agents;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Supplies;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.managers.Units.Filter;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedResources;
import org.ideasmashup.specialtactics.needs.NeedSupply;
import org.ideasmashup.specialtactics.needs.NeedUnit;

import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;

public class Base extends UnitAgent implements Producer, Consumer, UnitListener {

	protected LinkedList<NeedUnit> consumers;
	protected List<Need> needs; // cached copy of consumers needs

	protected boolean hasSupply;
	protected boolean hasResources;

	// current
	public Base(Unit base) {
		super(base);

		this.consumers = new LinkedList<NeedUnit>();
		this.needs = new LinkedList<Need>();

		this.hasResources = false;
		this.hasSupply = false;

		this.init();
	}

	protected void init() {
		// look for surrounding mineral patches
		// assign them MineralPatch agents asap ?

		// register itself to units events
		Units.getInstance().addListener(this);
		Needs.getInstance().add(this);
	}

	@Override
	public Need[] getNeeds(boolean returnAll) {
		if (returnAll) {
			return needs.toArray(new Need[0]);
		}
		else {
			List<Need> unsatisfied = new ArrayList<Need>();
			for (Need need : needs) {
				if (!need.isSatisfied()) unsatisfied.add(need);
			}
			return unsatisfied.toArray(new Need[0]);
		}
	}

	@Override
	public boolean fillNeeds(Object offer) {
		// typical Base needs are minerals and supply to produce workers
		// these needs are filled with a "null" offer

		System.out.println("Base.fillNeeds("+ offer +")");

		if (this.bindee.isTraining()) {
			// already training unit, do nothing to save resources
		}
		else if (this.bindee.isIdle()) {
			// not doing anything let's see if we can build something

			NeedUnit nu = consumers.peekFirst();
			if (nu != null) {

				// we have a consumer who want a worker
				System.out.println(" - base still has "+ consumers.size() +" workers consumers waiting");

				UnitType ut = nu.getUnitType();
				boolean needFilled = false;

				Resources res = Resources.getInstance();
				Supplies sup = Supplies.getInstance();

				if (!hasSupply && sup.getSupply(this) >= ut.supplyRequired()) {
					hasSupply = true;
					needFilled = true;

					// lock supply for this worker
					if (sup.hasReserved(this)) {
						sup.reserveSupply(ut.supplyRequired(), this);
					}
				}

				if (!hasResources
					&& res.getMinerals(this) >= ut.mineralPrice()
					&& res.getGas(this) >= ut.gasPrice()) {

					hasResources = true;
					needFilled = true;

					// lock resources for this worker
					if (!res.hasReserved(this)) {
						res.reserveMinerals(ut.mineralPrice(), this);
						res.reserveGas(ut.gasPrice(), this);
					}
				}

				if (hasSupply && hasResources) {

					System.out.println("  - has enough supply, resources, attempt building worker...");

					// can build unit (worker) for first consumer
					boolean building = bindee.train(ut);

					if (building) {
						System.out.println("   - SUCCESS : worker building, reseting for next worker");

						// reset flags for next worker building request
						hasSupply = false;
						hasResources = false;

						try {
							// can release all reserved resources
							res.unreserve(this);
							sup.unreserveSupply(this);

							// and remove consumer
							consumers.remove();
						}
						catch (Exception e) {
							e.printStackTrace();
						}

						System.out.println("   - BASE removed consumer, consumers waiting = "+ consumers.size());
					}
					else {
						// cannot build... ???
						System.err.println("ERROR: base couldn't build worker, will try again later...");
					}
				}
				else {
					// if one "new" (pending) need has been satisfied return true
					System.out.println(" - some needs still missing, return false");
					return needFilled;
				}
			}
			else {
			}
		}

		// no adequate offer yet (waiting for next offer to be satisfying)
		return false;
	}

	@Override
	public void onUnitDiscover(Unit unit) {}

	@Override
	public void onUnitEvade(Unit unit) {}

	@Override
	public void onUnitShow(Unit unit) {}

	@Override
	public void onUnitHide(Unit unit) {}

	@Override
	public void onUnitCreate(Unit unit) {
		// this base is being created ? maybe useless, depends if same Base
		// handles reconstruction?
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		// this base has been destroyed : release all consumers
		// remove producer, destroy this agent (? a new one will be added?)
	}

	@Override
	public void onUnitMorph(Unit unit) {
		// for zerg hatchery upgrades only !
	}

	@Override
	public void onUnitRenegade(Unit unit) {}

	@Override
	public void onUnitComplete(Unit unit) {
		// this base has just completed (maybe useful for zerg, maybe useful for
		// base rebuilding if we allow the same agent to persist across rebuilds

		double distance = unit.getPosition().getDistance(bindee.getPosition());
		if (distance < 400) {
			// FIXME magic number 400 should be replaced by radius calculation?
			//System.out.println(" - worker build at "+ distance +" from base");
			// this is a worker produced by us, so remove one consumer

		}
	}

	protected Filter filter = new Filter() {
		@Override
		public boolean allow(Unit unit) {
			return unit == bindee || Units.Types.WORKERS.is(unit);
		};
	};

	@Override
	public Filter getFilter() {
		return this.filter;
	}

	/* (non-Javadoc)
	 * @see org.ideasmashup.specialtactics.agents.Producer#canFill(org.ideasmashup.specialtactics.needs.Need)
	 */
	@Override
	public boolean canFill(Need need) {
		if (need instanceof NeedUnit) {
			NeedUnit nu = (NeedUnit) need;
			return Units.Types.WORKERS.contains(nu.getUnitType());
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.ideasmashup.specialtactics.agents.Producer#addConsumer(org.ideasmashup.specialtactics.agents.Consumer, org.ideasmashup.specialtactics.needs.Need)
	 */
	@Override
	public void addConsumer(Consumer consumer, Need need) {
		// add new consumer (a worker consumer)
		NeedUnit uneed = (NeedUnit) need;
		UnitType ut = uneed.getUnitType();

		// add the corresponding supply and resources needs
		Needs.getInstance().add(new NeedResources(this, ut.mineralPrice(), ut.gasPrice()));
		Needs.getInstance().add(new NeedSupply(this, ut.supplyRequired()));

		// add this consumer to the list
		this.consumers.add(uneed);
	}

}
