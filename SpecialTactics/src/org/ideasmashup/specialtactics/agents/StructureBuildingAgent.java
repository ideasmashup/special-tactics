package org.ideasmashup.specialtactics.agents;

import java.util.ArrayList;
import java.util.List;

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

public class StructureBuildingAgent extends MasterAgent implements Consumer {

	protected List<Need> needs;
	protected UnitType structureType;

	public StructureBuildingAgent(Unit bindee, UnitType structure, float priority) {
		super(bindee);

		this.servantsType = Units.Types.WORKERS;
		this.servants = new ArrayList<Unit>(2);
		this.needs = new ArrayList<Need>(2);

		this.structureType = structure;

		// ask for a worker to help build the structure
		initNeeds(priority);
		plugNeeds();
	}

	protected void initNeeds(float priority) {
		// we only need one worker to build the structure and the correct
		// amount of minerals
		this.needs.add(new NeedUnit(servantsType.getUnitType(), priority));
		this.needs.add(new NeedResources(structureType.mineralPrice(), structureType.gasPrice(), priority));
	}

	protected void plugNeeds() {
		for (Need need : needs) {
			if (!need.isSatisfied()) {
				Needs.add(need, this);
			}
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
		for (Need need : needs) {
			if (offer instanceof Unit) {
				Unit unit = (Unit) offer;
				if (unit.getType().isWorker()) {
					System.out.println("mineral patch just received new worker #"+ unit.getID() +"! yay!");
					need.setSatified(true);
					addServant(unit);

					// move the unit to the building location
					// FIXME for debugging now we just find a spot between the
					//       command-center in direction of the base chokepoint
					Chokepoint choke = BWTA.getNearestChokepoint(unit.getTilePosition());
					BaseLocation base = BWTA.getNearestBaseLocation(unit.getPosition());
					Position buildSite = new Position(
						(choke.getCenter().getX() + base.getPosition().getX()) / 2,
						(choke.getCenter().getY() + base.getPosition().getY()) / 2
					);

					unit.patrol(buildSite);

					return true;
				}
			}
			else {
				// check ressources, supply and non-unit needs
				if (Resources.getMinerals() >= structureType.mineralPrice()
						&& Resources.getGas() >= structureType.gasPrice()) {
					Resources.lockMinerals(structureType.mineralPrice(), true);
					Resources.lockGas(structureType.gasPrice(), true);

					// start building
					//StructureBuildingAgent sba = new StructureBuildingAgent(servants.get(0), structure., Need.HIGH);

					return true;
				}
			}
		}

		return false;
	}

}
