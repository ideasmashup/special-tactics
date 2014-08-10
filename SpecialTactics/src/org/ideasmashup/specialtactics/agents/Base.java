package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.brains.Units;
import org.ideasmashup.specialtactics.utils.UnitListener;
import org.ideasmashup.specialtactics.utils.Utils;

import bwapi.Unit;

public class Base extends MasterAgent implements UnitListener {

	public Base(Unit base) {
		super(base);
	}

	@Override
	protected void init() {
		// look for surrounding mineral patches
		// assign them MineralPatch agents asap

		//Units.get(Unit.Types.WORKERS);

		// attach itself to workers creation
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
				bindee.train(Units.Types.WORKERS.getUnitType());
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
