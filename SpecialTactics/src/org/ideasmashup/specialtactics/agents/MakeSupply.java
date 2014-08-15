package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.managers.Agents;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedResources;
import org.ideasmashup.specialtactics.needs.NeedUnit;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;




public class MakeSupply extends DefaultAgent implements Consumer {

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

		UnitType type = Units.Types.SUPPLY.getUnitType();
		Resources.getInstance().lockMinerals(type.mineralPrice(), true);
		Resources.getInstance().lockGas(type.gasPrice(), true);

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
			if (worker.getPosition() == pos) {
				// arrived at location
				System.out.println("SUPPLY : worker arrived at building site");
			}
			else {
				System.out.println("SUPPLY : worker moving to building site");
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
				BaseLocation bl = BWTA.getNearestBaseLocation(unit.getPosition());
				this.pos = new Position(
					cp.getCenter().getX() + bl.getPosition().getX() / 2,
					cp.getCenter().getY() + bl.getPosition().getY() / 2
				);
				unit.move(pos);

				System.out.println("SUPPLY : Moving worker #"+ unit.getID() +" to choke point!");

				return true;
			}
		}
		else {
			// non unit offer : assume minerals

		}

		return false;
	}
}
