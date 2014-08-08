package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.utils.UType;
import org.ideasmashup.specialtactics.utils.UnitListener;
import org.ideasmashup.specialtactics.utils.Utils;

import bwapi.Unit;
import bwta.BWTA;

public class Base extends MasterAgent implements UnitListener {

	public Base(Unit base) {
		super(base);
	}

	@Override
	protected void init() {
		// look for surrounding mineral patches
		// assign them MineralPatch agents asap

		// attach itself to workers creation
		Utils.get().addUnitsListener(Utils.get().getTypeFor(UType.WORKER), this);
	}

	@Override
	public void update() {
		super.update();

		// basic units building
		if (this.bindee.isTraining()) {
			// already training unit, do nothing because we don't queue thanks to
			// AI insane APMs
		}
		else {
			// not training unit let's see if we can build something

			// TODO replace with prioritized Needs collection (?)
			if (Utils.get().getPlayer().minerals() >= 50) {
				bindee.train(Utils.get().getTypeFor(UType.WORKER));
			}
		}


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
		// TODO Auto-generated method stub

	}

}
