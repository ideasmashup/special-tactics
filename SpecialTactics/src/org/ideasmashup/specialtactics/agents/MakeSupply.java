package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.managers.Agents;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedResources;
import org.ideasmashup.specialtactics.needs.NeedUnit;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.Chokepoint;




public class MakeSupply extends DefaultAgent implements Consumer, UnitListener {

	protected UnitType supplyType;
	protected Unit supply;
	protected Position pos;
	protected Unit worker;

	public MakeSupply() {
		super();
		this.pos = null;
		this.worker = null;
		this.supply = null;

		initNeeds();
	}

	protected void initNeeds() {
		// FIXME use polymorphism to implement race-specific
		//       supply creation agents (?)

		// first need a worker to build the depot it will be "booked temporarily"
		// so that the "next in line" consumer will already have it
		System.out.println("SUPPLY : Requested next worker for supply building");
		Needs.getInstance().add(new NeedUnit(Units.Types.WORKERS.getUnitType(), 0), this);

		// then we must "reserve" 100 minerals to be able to build the supply
		// FIXME ugly workaround to prevent minerals and gas for the pylons/dpot
		//       to be eaten up by other minerals consumers
		supplyType = Units.Types.SUPPLY.getUnitType();

		Resources.getInstance().reserveMinerals(supplyType.mineralPrice(), this);
		Resources.getInstance().reserveGas(supplyType.gasPrice(), this);

		System.out.println("SUPPLY : Requested ressources for supply building");
		Needs.getInstance().add(new NeedResources(
			Units.Types.SUPPLY.getUnitType().mineralPrice(),
			Units.Types.SUPPLY.getUnitType().gasPrice()
		), this);

		// auto-register agent
		Agents.getInstance().add(this);

		// auto-watch building construction
		Units.getInstance().addListener(this);
	}

	@Override
	public void update() {
		super.update();

		if (pos != null && worker != null && supply == null) {
			if (!worker.isConstructing()) {
				System.out.println("SUPPLY : worker moving to choke and trying to build somewhere on that route");
				TilePosition tp = worker.getTilePosition();

				if (AI.getGame().canBuildHere(worker, tp, supplyType)) {
					// when moving try to find a place where a supply structure can be built
					worker.build(tp, supplyType);
				}
				else {
					worker.move(pos);
				}
			}
		}
	}

	@Override
	public Need[] getNeeds(boolean returnAll) {
		return new Need[0];
	}

	@Override
	public boolean fillNeeds(Object offer) {
		if (offer instanceof Unit) {
			Unit unit = (Unit) offer;
			if (unit.getType().isWorker()) {
				System.out.println("SUPPLY : Received new worker #"+ unit.getID() +"!");

				// assign worker
				this.worker = unit;

				// force unit to patrol to choke
				Chokepoint cp = BWTA.getNearestChokepoint(unit.getPosition());
				this.pos = new Position(cp.getCenter().getX(), cp.getCenter().getY());

				System.out.println("SUPPLY : Moving worker #"+ unit.getID() +" to choke point!");
				worker.move(pos);

				return true;
			}
		}
		else {
			// non-unit offer : assume minerals
		}

		return false;
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
		if (unit.getType() == supplyType && unit.isBeingConstructed()) {
			// this may be the depot this worker is building

			// check by identifying the depot,
			if (unit.getBuildUnit() == worker) {
				System.out.println("SUPPLY : the worker is constructing "+ unit.getType());
				supply = unit;
			}
			else {

			}
		}
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
		if (unit == supply) {
			// the supply depot, pylon... has been built!
			System.out.println("SUPPLY : supply structure created !");

			// remove from managers
			Agents.getInstance().remove(this);
			Units.getInstance().removeListener(this);

			// kill this agent and free its worker
			this.destroy();
			Units.getInstance().onUnitComplete(worker);

			// nullify refs
			this.worker = null;
			this.supply = null;
			this.pos = null;

		}
	}
}
