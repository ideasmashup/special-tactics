package org.ideasmashup.specialtactics.brains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ideasmashup.specialtactics.utils.UnitListener;

import bwapi.Unit;
import bwapi.UnitType;

/**
 * Units collection and watcher.
 *
 * Stores all units mapped by types. Provides convenience methods to
 * <ul>
 * <li>assign meta-data to any unit</li>
 * <li>assign agents to any unit</li>
 * <li>assign</li>
 * </ul>
 *
 * All units managed by this class are automatically added/removed when
 * destroyed/created.
 *
 *
 * @author William ANGER <github at ideasmashup.com>
 *
 */
public class Units {
	protected Map<Types, ArrayList<Unit>> map;

	public Units() {
		this.map = new HashMap<Types, ArrayList<Unit>>();
	}

	public void add(Unit unit) {
		Types[] types = Types.getTypes(unit);
		for (Types type : types) {
			if (map.containsKey(type)) {
				map.get(type).add(unit);
			}
			else {
				ArrayList<Unit> arr = new ArrayList<Unit>();
				arr.add(unit);

				map.put(type, arr);
			}
		}
	}

	public void addListener(UnitListener ls) {

	}

	public static enum Types {
		WORKERS(
			new UnitType[]{UnitType.Protoss_Probe},
			new UnitType[]{UnitType.Terran_SCV},
			new UnitType[]{UnitType.Zerg_Drone}
		),
		BUILDING_BASE(
			new UnitType[]{UnitType.Protoss_Nexus},
			new UnitType[]{UnitType.Terran_Command_Center},
			new UnitType[]{
				UnitType.Zerg_Hatchery,
				UnitType.Zerg_Lair,
				UnitType.Zerg_Hive
			}
		),
		BUILDING_PRODUCTION(
			new UnitType[]{
				UnitType.Protoss_Gateway,
				UnitType.Protoss_Robotics_Facility,
				UnitType.Protoss_Stargate,
			},
			new UnitType[]{},
			new UnitType[]{}
		),
		BUILDING_DEFENCE(
			new UnitType[]{
				UnitType.Protoss_Photon_Cannon,
			},
			new UnitType[]{},
			new UnitType[]{}
		),
		BUILDING_DETECTION(
			new UnitType[]{
				UnitType.Protoss_Photon_Cannon,
			},
			new UnitType[]{},
			new UnitType[]{}
		),
		MILITARY_CASTERS(
			new UnitType[]{
				UnitType.Protoss_High_Templar,
				UnitType.Protoss_Dark_Archon,
			},
			new UnitType[]{},
			new UnitType[]{}
		),
		MILITARY_GROUND(
			new UnitType[]{

			},
			new UnitType[]{},
			new UnitType[]{}
		),
		MILITARY_GROUND_RANGED(
			new UnitType[]{},
			new UnitType[]{},
			new UnitType[]{}
		),
		MILITARY_GROUND_MELEE(
			new UnitType[]{},
			new UnitType[]{},
			new UnitType[]{}
		),
		MILITARY_AIR(
			new UnitType[]{},
			new UnitType[]{},
			new UnitType[]{}
		),
		MILITARY_AIR_RANGED(
			new UnitType[]{},
			new UnitType[]{},
			new UnitType[]{}
		),
		MILITARY_AIR_MELEE(
			new UnitType[]{},
			new UnitType[]{},
			new UnitType[]{}
		);

		Types(UnitType[] protoss, UnitType[] terran, UnitType[] zerg) {
			this.protoss = protoss;
			this.terran = terran;
			this.zerg = zerg;
		}

		private UnitType[] protoss;
		private UnitType[] terran;
		private UnitType[] zerg;

		public UnitType[] get(Race race) {
			if (race == Race.Protoss) {
				return protoss;
			}
			else if (race == Race.Terran) {
				return terran;
			}
			else if (race == Race.Zerg) {
				return zerg;
			}

			return null;
		}

		public UnitType getUnitType() {
			UnitType[] ut = get(Utils.get().getPlayer().getRace());
			if (ut.length > 0) {
				return ut[0];
			}
			return null;
		}

		public boolean is(Unit unit) {
			for (int i=0; i<protoss.length; i++) {
				if (protoss[i] == unit.getType()) return true;
			}
			for (int i=0; i<terran.length; i++) {
				if (protoss[i] == unit.getType()) return true;
			}
			for (int i=0; i<zerg.length; i++) {
				if (protoss[i] == unit.getType()) return true;
			}

			return false;
		}

		public static Types[] getTypes(Unit unit){
			List<Types> types = new LinkedList<Types>();
			for (Types type : Types.values()) {
				if (type.is(unit)) {
					types.add(type);
				}
			}

			return types.toArray(new Types[0]);
		}

	}
}
