package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.utils.Utils;

import bwapi.Race;
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

	protected List<Integer> ids;
	protected Map<Types, ArrayList<Unit>> map;
	protected List<UnitListener> listeners;

	protected static Units instance = null;

	protected Units() {
		ids = new ArrayList<Integer>();
		map = new HashMap<Types, ArrayList<Unit>>();
		listeners = new ArrayList<UnitListener>();
	}

	public static void init() {
		if (instance == null) {
			instance = new Units();

			System.out.println("Units initialized");
		}
	}

	public static void add(Unit unit) {
		//if (!ids.contains(unit.getID())) {
			System.out.println("unit : #"+ unit.getID() + " ("+ unit.getType() +") newly registered");

			Types[] types = Types.getTypes(unit);
			for (Types type : types) {
				System.out.println(" - assigned type ("+ type.name() +")");
				if (instance.map.containsKey(type)) {
					instance.map.get(type).add(unit);
				}
				else {
					ArrayList<Unit> arr = new ArrayList<Unit>();
					arr.add(unit);

					instance.map.put(type, arr);
				}
			}
		//}
		//else {
		//	System.out.println("unit : #"+ unit.getID() + " ("+ unit.getType() +") already registered");
		//}
	}

	public static void addListener(UnitListener ls) {
		instance.listeners.add(ls);
	}

	public static void removeListener(UnitListener ls) {
		instance.listeners.remove(ls);
	}

	public static void removeAllListeners() {
		instance.listeners.clear();
	}

	public static Unit[] get(Types type) {
		return instance.map.get(type).toArray(new Unit[0]);
	}

	public static enum Types {
		WORKERS(
			new UnitType[]{UnitType.Protoss_Probe},
			new UnitType[]{UnitType.Terran_SCV},
			new UnitType[]{UnitType.Zerg_Drone}
		),
		SUPPLY(
			new UnitType[]{UnitType.Protoss_Pylon},
			new UnitType[]{UnitType.Terran_Supply_Depot},
			new UnitType[]{UnitType.Zerg_Overlord}
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

		private final UnitType[] protoss;
		private final UnitType[] terran;
		private final UnitType[] zerg;

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

	public static void onUnitDiscover(Unit unit) {
		// call all listeners
		for (UnitListener ls : instance.listeners) {
			ls.onUnitDiscover(unit);
		}
	}

	public static void onUnitEvade(Unit unit) {
		// call all listeners
		for (UnitListener ls : instance.listeners) {
			ls.onUnitEvade(unit);
		}
	}

	public static void onUnitShow(Unit unit) {
		// call all listeners
		for (UnitListener ls : instance.listeners) {
			ls.onUnitShow(unit);
		}
	}

	public static void onUnitHide(Unit unit) {
		// call all listeners
		for (UnitListener ls : instance.listeners) {
			ls.onUnitHide(unit);
		}
	}

	public static void onUnitCreate(Unit unit) {
		System.out.println("Units.onUnitCreate()");
		System.out.println("Units.listeners.size() = "+ instance.listeners.size());
		// call all listeners
		for (UnitListener ls : instance.listeners) {
			ls.onUnitCreate(unit);
		}
	}

	public static void onUnitDestroy(Unit unit) {
		System.out.println("Units.onUnitDestroy()");
		// call all listeners
		for (UnitListener ls : instance.listeners) {
			ls.onUnitDestroy(unit);
		}
	}

	public static void onUnitMorph(Unit unit) {
		// call all listeners
		for (UnitListener ls : instance.listeners) {
			ls.onUnitMorph(unit);
		}
	}

	public static void onUnitRenegade(Unit unit) {
		// call all listeners
		for (UnitListener ls : instance.listeners) {
			ls.onUnitRenegade(unit);
		}
	}

	/**
	 * Tells the units manager a new unit has been built.
	 *
	 * This method is called automatically whenever a new unit is produced by
	 * a building, factory, egg, etc...
	 *
	 * You can also call this method on units that you no longer need and want
	 * to give back to the others {@link UnitConsumer}.
	 *
	 * @param unit the {@link Unit} twhich is newly available for "consumption"
	 */
	public static void onUnitComplete(Unit unit) {
		// call all listeners
		for (UnitListener ls : instance.listeners) {
			ls.onUnitComplete(unit);
		}
	}

	// FIXME implement these and add them to the UnitListener interface

	public static void onUnitAttacked(Unit unit) {
		//
	}
}
