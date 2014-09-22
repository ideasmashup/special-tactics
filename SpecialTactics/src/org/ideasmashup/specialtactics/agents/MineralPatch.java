package org.ideasmashup.specialtactics.agents;

import java.util.ArrayList;
import java.util.List;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Tiles;
import org.ideasmashup.specialtactics.managers.Tiles.Specs;
import org.ideasmashup.specialtactics.managers.Tiles.Trails;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedUnit;
import org.ideasmashup.specialtactics.tiles.Tile;

import bwapi.Color;
import bwapi.Unit;
import bwta.BWTA;

public class MineralPatch extends MasterAgent implements Consumer {

	protected List<Need> needs; // each patch needs 2 workers for saturation

	public MineralPatch(Unit mineralpatch) {
		super(mineralpatch);

		this.servantsType = Units.Types.WORKERS;
		this.servants = new ArrayList<Unit>(2);
		this.needs = new ArrayList<Need>(2);

		// initialize needs and register them to the global Needs manager
		// so that they can be satisfied automatically asap

		initNeeds();
		plugNeeds();
	}

	protected void initNeeds() {
		// mineral patch needs two workers (to be assigned as miners "servants")
		// priority is defined based on distance from nearest base location

		// FIXME should invert distance (more distance => lower priority)
		float distance = bindee.getDistance(BWTA.getNearestBaseLocation(bindee.getPosition()).getPosition());

		this.needs.add(new NeedUnit(this, servantsType.getUnitType(), Need.LOW + distance));
		this.needs.add(new NeedUnit(this, servantsType.getUnitType(), Need.LOW + distance));
		this.needs.add(new NeedUnit(this, servantsType.getUnitType(), Need.LOW + distance));
	}

	protected void plugNeeds() {
		for (Need need : needs) {
			if (!need.isSatisfied()) {
				Needs.getInstance().addNeed(need);
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
	public boolean canReceiveOffer() {
		return !bindee.isBeingGathered();
	}

	@Override
	public boolean fillNeeds(Object offer) {
		for (Need need : needs) {
			if (offer instanceof Unit) {
				Unit unit = (Unit) offer;
				if (unit.getType().isWorker()) {
					System.out.println("mineral patch just received new worker #"+ unit.getID() +"! yay!");
					need.setSatisfied(true);
					addServant(unit);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void update() {
		if (bindee.getResources() == 0) {
			System.out.println("mineral #"+bindee+" mined out !!");
			// no longer mineable
			// FIXME replace this with onUnitDestroyed(bindee)
			//       now this bug because getRessources() returns 0 sometimes!!
			freeAllServants();
		}
		else {
			// can still be mined : mining micro-management of all servants
			for (Unit servant : servants) {
				AI.getGame().drawLineMap(servant.getPosition().getX(), servant.getPosition().getY(), bindee.getPosition().getX(), bindee.getPosition().getY(), Color.Blue);

				if (servant.isGatheringMinerals()) {
					// collecting minerals so keep doing it
					//System.out.println("MineralPatch : worker #"+ servant.getID()+" ignored because it's mining something");
				}
				else if (servant.isIdle()) {// || !servant.isMoving()) {
					if (servant.isCarryingMinerals()) {
						//System.out.println("MineralPatch : worker #"+ servant.getID()+" with mineral moved back to base");
						// carrying minerals so move back to base
						// FIXME maybe this is already automatic ?
						servant.returnCargo();
					}
					else {
						// not doing anything so move back to mineral collection on this patch
						//System.out.println("MineralPatch : worker #"+ servant.getID()+" forced to mine this patch");
						servant.gather(bindee);
					}
				}

				// FIXME only for debugging of Tiles editing
				if (servant.isMoving() && servant.isCarryingMinerals()) {
					// create a mineral trail and turn underneath tiles into unbuildable tiles
					Tile tileBuild = Tiles.getInstance().getBuildTile(servant.getPosition());
					Tile tileUnits = Tiles.getInstance().getUnitTile(servant.getPosition());

					tileUnits.getTrail(Trails.TRAIL_MINING).increase();
					tileBuild.setSpecs(Specs.BUILDABLE, false);
				}
			}
		}
	}
}
