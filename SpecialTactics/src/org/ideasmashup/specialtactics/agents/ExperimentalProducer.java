package org.ideasmashup.specialtactics.agents;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Producers;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Supplies;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.managers.Units.Filter;
import org.ideasmashup.specialtactics.managers.Units.Types;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedResources;
import org.ideasmashup.specialtactics.needs.NeedSupply;
import org.ideasmashup.specialtactics.needs.NeedUnit;

import bwapi.Color;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;

public class ExperimentalProducer extends UnitAgent implements Producer, Consumer, UnitListener {

	protected LinkedList<NeedUnit> consumers;
	protected List<Need> needs; // cached copy of consumers needs

	protected boolean hasSupply;
	protected boolean hasResources;

	public ExperimentalProducer(Unit bindee) {
		super(bindee);

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
		Producers.getInstance().addProducer(this);

		// set rallypoint to choke
		//Chokepoint cp = BWTA.getNearestChokepoint(bindee.getPosition());
		//bindee.setRallyPoint(cp.getCenter());


		// request new zealots to itself
		/*
		Needs.getInstance().addNeed(new NeedUnit(this, Types.GROUND_T1.getUnitType()));
		Needs.getInstance().addNeed(new NeedUnit(this, Types.GROUND_T1.getUnitType()));
		Needs.getInstance().addNeed(new NeedUnit(this, Types.GROUND_T1.getUnitType()));
		Needs.getInstance().addNeed(new NeedUnit(this, Types.GROUND_T1.getUnitType()));
		Needs.getInstance().addNeed(new NeedUnit(this, Types.GROUND_T1.getUnitType()));
		Needs.getInstance().addNeed(new NeedUnit(this, Types.GROUND_T1.getUnitType()));
		Needs.getInstance().addNeed(new NeedUnit(this, Types.GROUND_T1.getUnitType()));
		Needs.getInstance().addNeed(new NeedUnit(this, Types.GROUND_T1.getUnitType()));
		Needs.getInstance().addNeed(new NeedUnit(this, Types.GROUND_T1.getUnitType()));
		*/
	}

	@Override
	public void update() {
		super.update();

		Resources res = Resources.getInstance();
		UnitType type = Types.GROUND_T1.getUnitType();

		if (bindee.isIdle() && res.getMinerals() >= type.mineralPrice()) {
			bindee.train(type);
		}
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
		// typical needs are minerals and supply to produce units
		// these needs are filled with a "null" offer

		System.out.println("Producer "+ bindee + ".fillNeeds("+ offer +")");

		if (this.bindee.isTraining()) {
			// already training unit, do nothing to save resources
		}
		else if (this.bindee.isIdle()) {
			// not doing anything let's see if we can build something

			if (!consumers.isEmpty()) {
				NeedUnit nu = consumers.getFirst();

				// we have a consumer who want a worker
				System.out.println(" - still has "+ consumers.size() +" consumers waiting");

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

					System.out.println("  - has enough supply, resources, attempt building...");

					// can build unit (worker) for first consumer
					boolean building = bindee.train(ut);

					if (building) {
						System.out.println("   - SUCCESS : building, reseting for next worker");

						// reset flags for next worker building request
						hasSupply = false;
						hasResources = false;

						try {
							// can release all reserved resources
							res.unreserve(this);
							sup.unreserveSupply(this);

							// and remove consumer
							consumers.removeFirst();
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
				System.out.println(" - base has no more waiting consumers... production stopped");
			}
		}

		// no adequate offer yet (waiting for next offer to be satisfying)
		return false;
	}


	@Override
	public boolean canFill(Need need) {
		// FIXME super-basic production... this is awful, should be rewritten asap!
		if (need instanceof NeedUnit) {
			NeedUnit nu = (NeedUnit) need;
			return bindee.train(nu.getUnitType());
		}
		return false;
	}

	@Override
	public void addConsumer(Consumer consumer, Need need) {
		// add new consumer (a worker consumer)
		NeedUnit nu = (NeedUnit) need;
		UnitType ut = nu.getUnitType();

		// add the corresponding supply and resources needs
		Needs.getInstance().addNeed(new NeedResources(this, ut.mineralPrice(), ut.gasPrice()));
		Needs.getInstance().addNeed(new NeedSupply(this, ut.supplyRequired()));

		// add this consumer to the list
		this.consumers.add(nu);

		System.out.println(" - Producer : "+ bindee +" new consumer added, consumers total = "+ consumers.size());
	}

	@Override
	public void onUnitDiscover(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitEvade(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitShow(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitHide(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitCreate(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitDestroy(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitMorph(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitRenegade(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitComplete(Unit unit) {
		if (Types.GROUND_T1.is(unit)) {
			// attack to natural
			Position pos = BWTA.getNearestChokepoint(bindee.getPosition()).getCenter();
			AI.getGame().drawLineMap(unit.getPosition().getX(), unit.getPosition().getY(), pos.getX(), pos.getY(), Color.Red);
			unit.attack(pos);
		}

	}

	protected Filter filter = new Filter() {
		@Override
		public boolean allow(Player player) {
			return player == AI.getPlayer();
		}

		@Override
		public boolean allow(Unit unit) {
			return Types.GROUND_T1.is(unit) || Types.PROD_T1.is(unit);
		}
	};


	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return filter;
	}


}
