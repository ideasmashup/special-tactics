package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.managers.Agents;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedResources;
import org.ideasmashup.specialtactics.needs.NeedUnit;
import org.ideasmashup.specialtactics.utils.Utils;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.Chokepoint;




public class MakeSupply extends DefaultAgent implements Consumer {

	protected UnitType supplyType;
	protected Position pos;
	protected Unit worker;

	public MakeSupply() {
		super();
		this.pos = null;
		this.worker = null;

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

		Resources.getInstance().lockMinerals(supplyType.mineralPrice(), true);
		Resources.getInstance().lockGas(supplyType.gasPrice(), true);

		System.out.println("SUPPLY : Requested ressources for supply building");
		Needs.getInstance().add(new NeedResources(
			Units.Types.SUPPLY.getUnitType().mineralPrice(),
			Units.Types.SUPPLY.getUnitType().gasPrice()
		), this);

		// auto-register agent
		Agents.getInstance().add(this);
	}

	@Override
	public void update() {
		super.update();

		if (pos != null && worker != null) {
			if (!worker.isConstructing()) {
				System.out.println("SUPPLY : worker moving to choke and trying to build somewhere on that route");
				TilePosition tp = worker.getTilePosition();

				if (Utils.get().getGame().canBuildHere(worker, tp, supplyType)) {
					// when moving try to find a place where a supply structure can be built
					worker.build(tp, supplyType);
				}
				else {
					worker.move(pos);
				}
			}
			else if (worker.isIdle()) {
				// done building must free the worker and kill this agent
				this.destroy();
				this.worker = null;
				Units.getInstance().onUnitComplete(worker);
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
			// non unit offer : assume minerals

		}

		return false;
	}
}
