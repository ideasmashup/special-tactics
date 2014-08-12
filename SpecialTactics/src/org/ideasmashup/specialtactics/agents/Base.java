package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.listeners.ResourcesListener;
import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Units;

import bwapi.Unit;

public class Base extends MasterAgent implements UnitListener, ResourcesListener {

	public Base(Unit base) {
		super(base);
	}

	@Override
	protected void init() {
		// look for surrounding mineral patches
		// assign them MineralPatch agents asap ?

		// register itself to units events, resources events
		Units.addListener(this);
		Resources.addListener(this);
	}

	@Override
	public void update() {
		super.update();
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

	@Override
	public void onResourcesChange(int minerals, int gas) {
		System.out.println("base onResourcesChange("+minerals+")");

		// basic units building
		if (this.bindee.isTraining()) {
			// already training unit, do nothing because we don't queue thanks to
			// AI insane APMs
		}
		else if (this.bindee.isIdle()) {
			// not doing anything let's see if we can build something

			// TODO replace with prioritized Needs collection (?)
			if (minerals >= 50) {
				bindee.train(Units.Types.WORKERS.getUnitType());
			}
		}
	}


}
