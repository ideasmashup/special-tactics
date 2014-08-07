package org.ideasmashup.specialtactics.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedServant;
import org.ideasmashup.specialtactics.utils.UType;
import org.ideasmashup.specialtactics.utils.Utils;

import bwapi.Unit;
import bwta.BWTA;

public class MineralPatch {

	protected Unit bindee; // the mineral patch
	protected List<Unit> servants; // the workers assigned to this patch
	protected List<Need> needs; // the request to have 2 workers

	public MineralPatch() {
		this.servants = new ArrayList<Unit>(2);
		this.needs = new ArrayList<Need>(2);

		initNeeds();
	}

	protected void initNeeds() {
		// mineral patch needs two workers (to be assigned as miners "servants")
		// priority is defined based on distance from nearest base location
		float priority = 1/bindee.getDistance(BWTA.getNearestBaseLocation(bindee.getPosition()).getPosition());
		this.needs.add(new NeedServant(Utils.get().getTypeFor(UType.WORKER), priority));
		this.needs.add(new NeedServant(Utils.get().getTypeFor(UType.WORKER), priority));
	}

	public void update() {
		if (bindee.getResources() == 0) {
			// no longer mineable
			freeAllServants();
		}
		else {
			// can still be mined : mining micro-management of all servants
			for (Unit servant : servants) {
				if (servant.isCarryingMinerals()) {
					// carrying minerals so move back to base
					// FIXME maybe this is already automatic ?
					servant.move(BWTA.getNearestBaseLocation(servant.getPosition()).getPosition());
				}
				else if (servant.isGatheringMinerals()) {
					// collecting minerals so keep doing it
				}
				else {
					// not doing anything so move back to mineral collection on this patch
					servant.gather(bindee);
				}
			}
		}
	}

	public List<Need> getNeeds() {
		return Collections.unmodifiableList(needs);
	}

	public boolean fillNeed(Need need, Unit offer) {
		if (need instanceof Need && offer.getType().isWorker()) {// ServantNeed
			addServant(offer);
			need.setSatified(true);
		}

		return false;
	}

	protected void addServant(Unit unit) {

	}

	protected void freeServant(Unit unit) {
		servants.remove(unit);
		// Utils.getProductChannels().add(unit);
	}

	protected void freeAllServants() {

	}

}
