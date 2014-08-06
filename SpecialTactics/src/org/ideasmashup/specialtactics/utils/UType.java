package org.ideasmashup.specialtactics.utils;

import bwapi.Race;
import bwapi.UnitType;

public enum UType {
	WORKER (UnitType.Terran_SCV, UnitType.Protoss_Probe, UnitType.Zerg_Drone),
	T1 (UnitType.Terran_Marine, UnitType.Protoss_Zealot, UnitType.Zerg_Zergling);

	private UnitType terran;
	private UnitType protoss;
	private UnitType zerg;

	UType (UnitType terran, UnitType protoss, UnitType zerg) {
		this.terran = terran;
		this.protoss = protoss;
		this.zerg = zerg;
	}

	public UnitType get(Race race) {
		if (race == Race.Terran) {
			return terran;
		}
		else if (race == Race.Protoss) {
			return protoss;
		}
		else if (race == Race.Protoss) {
			return zerg;
		}
		else {
			return null;
		}
	}
}
