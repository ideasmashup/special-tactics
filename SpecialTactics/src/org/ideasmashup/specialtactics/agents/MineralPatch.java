package org.ideasmashup.specialtactics.agents;

import java.util.ArrayList;
import java.util.List;

import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedServant;
import org.ideasmashup.specialtactics.utils.UType;
import org.ideasmashup.specialtactics.utils.Utils;

import bwapi.Unit;

public class MineralPatch {

	protected Unit bindee; // the mineral patch
	protected List<Unit> servants; // the workers assigned to this patch

	public MineralPatch() {
		this.servants = new ArrayList<Unit>(3);
	}

	public void update() {
		// check if the
		if (bindee.getResources() < 10) {

		}
	}

	public Need[] getNeeds() {
		return new Need[]{new NeedServant(Utils.get().getTypeFor(UType.WORKER))};
	}

	public void addServant(Unit unit) {

	}

	public void freeServant(Unit unit) {

	}

	public void freeAllServants() {

	}

}
