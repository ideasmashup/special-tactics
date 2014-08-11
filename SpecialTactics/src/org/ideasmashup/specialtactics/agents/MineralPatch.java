package org.ideasmashup.specialtactics.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ideasmashup.specialtactics.brains.Units;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedUnit;
import org.ideasmashup.specialtactics.needs.Needs;

import bwapi.Unit;
import bwta.BWTA;

public class MineralPatch extends MasterAgent implements UnitConsumer {

	protected List<Need> needs; // the request to have 2 workers

	public MineralPatch(Unit mineralpatch) {
		super(mineralpatch);
		this.servants = new ArrayList<Unit>(2);
		this.needs = new ArrayList<Need>(2);

		// initialize this mineral patches needs
		// (e.g. 2 workers assigned for optimal saturation)
		initNeeds();

		// now ask the Needs manager to satisfy our current needs
		requestNeeds();
	}

	protected void initNeeds() {
		// mineral patch needs two workers (to be assigned as miners "servants")
		// priority is defined based on distance from nearest base location

		// FIXME should invert distance (more distance => lower priority)
		float priority = bindee.getDistance(BWTA.getNearestBaseLocation(bindee.getPosition()).getPosition());

		this.needs.add(new NeedUnit(Units.Types.WORKERS.getUnitType(), priority));
		this.needs.add(new NeedUnit(Units.Types.WORKERS.getUnitType(), priority));
	}

	protected void requestNeeds() {
		for (Need need : needs) {
			if (!need.isSatisfied()) {
				Needs.add(need, this);
			}
		}
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
				if (servant.isGatheringMinerals()) {
					// collecting minerals so keep doing it
					//System.out.println("MineralPatch : worker #"+ servant.getID()+" ignored because it's mining something");
				}
				else if (servant.isIdle()) {
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
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.ideasmashup.specialtactics.agents.UnitConsumer#getNeeds()
	 */
	@Override
	public List<Need> getNeeds() {
		return Collections.unmodifiableList(needs);
	}

	/* (non-Javadoc)
	 * @see org.ideasmashup.specialtactics.agents.UnitConsumer#fillNeed(org.ideasmashup.specialtactics.needs.Need, bwapi.Unit)
	 */
	@Override
	public boolean fillNeed(Unit offer) {
		for (Need need : needs) {
			if (offer.getType().isWorker()) {
				System.out.println("mineral patch just received new worker #"+ offer.getID() +"! yay!");
				need.setSatified(true);
				addServant(offer);
				return true;
			}
		}

		return false;
	}

}
