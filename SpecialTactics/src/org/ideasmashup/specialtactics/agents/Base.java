package org.ideasmashup.specialtactics.agents;

import java.util.ArrayList;
import java.util.List;

import org.ideasmashup.specialtactics.listeners.ResourcesListener;
import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedUnit;

import bwapi.Unit;
import bwta.BWTA;

public class Base extends MasterAgent implements Consumer, UnitListener, ResourcesListener {

	protected List<Need> needs; // needs 50 minerals

	public Base(Unit base) {
		super(base);

		this.servantsType = Units.Types.WORKERS;
	}

	@Override
	protected void init() {
		// look for surrounding mineral patches
		// assign them MineralPatch agents asap ?

		// register itself to units events, resources events
		Units.getInstance().addListener(this);
		Resources.getInstance().addListener(this);

		initNeeds();
		plugNeeds();
	}

	protected void initNeeds() {
		// mineral patch needs two workers (to be assigned as miners "servants")
		// priority is defined based on distance from nearest base location

		// FIXME should invert distance (more distance => lower priority)
		float priority = bindee.getDistance(BWTA.getNearestBaseLocation(bindee.getPosition()).getPosition());

		this.needs.add(new NeedUnit(servantsType.getUnitType(), priority));
		this.needs.add(new NeedUnit(servantsType.getUnitType(), priority));
	}

	protected void plugNeeds() {
		for (Need need : needs) {
			if (!need.isSatisfied()) {
				Needs.getInstance().add(need, this);
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
		// basic units building

		if (this.bindee.isTraining()) {
			// already training unit, do nothing because we don't queue thanks to
			// AI insane APMs
		}
		else if (this.bindee.isIdle()) {
			// not doing anything let's see if we can build something

			// TODO replace with prioritized Needs collection (?)
			if (Resources.getInstance().getMinerals() >= 50) {
				bindee.train(Units.Types.WORKERS.getUnitType());
			}
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
		//System.out.println("base onResourcesChange("+minerals+")");

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
