package org.ideasmashup.specialtactics.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.listeners.UnitListener;

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

	public static Units getInstance() {
		if (instance == null) {
			instance = new Units();

			System.out.println("Units initialized");
		}

		return instance;
	}

	public void add(Unit unit) {
		//if (!ids.contains(unit.getID())) {
			System.out.println("unit : #"+ unit.getID() + " ("+ unit.getType() +") newly registered");

			Types[] types = Types.getTypes(unit);
			for (Types type : types) {
				System.out.println(" - assigned type ("+ type.name() +")");
				if (map.containsKey(type)) {
					map.get(type).add(unit);
				}
				else {
					ArrayList<Unit> arr = new ArrayList<Unit>();
					arr.add(unit);

					map.put(type, arr);
				}
			}
		//}
		//else {
		//	System.out.println("unit : #"+ unit.getID() + " ("+ unit.getType() +") already registered");
		//}
	}

	public void addListener(UnitListener ls) {
		listeners.add(ls);
	}

	public void removeListener(UnitListener ls) {
		listeners.remove(ls);
	}

	public void removeAllListeners() {
		listeners.clear();
	}

	public Unit[] get(Types type) {
		return map.get(type).toArray(new Unit[0]);
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
		BASE(
			new UnitType[]{UnitType.Protoss_Nexus},
			new UnitType[]{UnitType.Terran_Command_Center},
			new UnitType[]{
				UnitType.Zerg_Hatchery,
				UnitType.Zerg_Lair,
				UnitType.Zerg_Hive
			}
		),
		PROD(
			new UnitType[]{
				UnitType.Protoss_Gateway,
				UnitType.Protoss_Robotics_Facility,
				UnitType.Protoss_Stargate,
			},
			new UnitType[]{},
			new UnitType[]{}
		),
		PROD_T1(
			new UnitType[]{UnitType.Protoss_Gateway},
			new UnitType[]{UnitType.Terran_Barracks},
			new UnitType[]{UnitType.Zerg_Hatchery}
		),
		TECH(
			new UnitType[]{
				UnitType.Protoss_Citadel_of_Adun,
			},
			new UnitType[]{
				UnitType.Terran_Academy,
			},
			new UnitType[]{
				UnitType.Zerg_Spawning_Pool,
			}
		),
		DEFENCE(
			new UnitType[]{
				UnitType.Protoss_Photon_Cannon,
			},
			new UnitType[]{},
			new UnitType[]{}
		),
		DETECTION(
			new UnitType[]{
				UnitType.Protoss_Photon_Cannon,
			},
			new UnitType[]{},
			new UnitType[]{}
		),
		CASTERS(
			new UnitType[]{
				UnitType.Protoss_High_Templar,
				UnitType.Protoss_Dark_Archon,
			},
			new UnitType[]{},
			new UnitType[]{}
		),
		GROUND(
			new UnitType[]{

			},
			new UnitType[]{},
			new UnitType[]{}
		),
		GROUND_RANGED(
			new UnitType[]{},
			new UnitType[]{},
			new UnitType[]{}
		),
		GROUND_MELEE(
			new UnitType[]{},
			new UnitType[]{},
			new UnitType[]{}
		),
		AIR(
			new UnitType[]{},
			new UnitType[]{},
			new UnitType[]{}
		),
		AIR_RANGED(
			new UnitType[]{},
			new UnitType[]{},
			new UnitType[]{}
		),
		AIR_MELEE(
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
			UnitType[] ut = get(AI.getPlayer().getRace());
			if (ut.length > 0) {
				return ut[0];
			}
			return null;
		}

		public boolean contains(UnitType ut) {
			UnitType[] uts = get(AI.getPlayer().getRace());
			for (UnitType u : uts) {
				if (ut == u) return true;
			}
			return false;
		}

		public boolean is(Unit unit) {
			return is(unit.getType());
		}

		public boolean is(UnitType ut) {
			for (int i=0; i<protoss.length; i++) {
				if (protoss[i] == ut) return true;
			}
			for (int i=0; i<terran.length; i++) {
				if (terran[i] == ut) return true;
			}
			for (int i=0; i<zerg.length; i++) {
				if (zerg[i] == ut) return true;
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
			return null;
		}

		public static Types[] getTypes(UnitType ut){
			List<Types> types = new LinkedList<Types>();
			for (Types type : Types.values()) {
				if (type.is(ut)) {
					types.add(type);
				}
			}
			return null;
		}

		public static Types getType(UnitType ut){
			for (Types type : Types.values()) {
				if (type.is(ut)) {
					return type;
				}
			}
			return null;
		}

		public static Types getType(Unit unit){
			for (Types type : Types.values()) {
				if (type.is(unit)) {
					return type;
				}
			}
			return null;
		}
	}


			return types.toArray(new Types[0]);
		}

	}

	public void onUnitDiscover(Unit unit) {
		// call all listeners
		for (UnitListener ls : listeners) {
			if (ls.getFilter().allow(unit))
				ls.onUnitDiscover(unit);
		}
	}

	public void onUnitEvade(Unit unit) {
		// call all listeners
		for (UnitListener ls : listeners) {
			if (ls.getFilter().allow(unit))
				ls.onUnitEvade(unit);
		}
	}

	public void onUnitShow(Unit unit) {
		// call all listeners
		for (UnitListener ls : listeners) {
			if (ls.getFilter().allow(unit))
				ls.onUnitShow(unit);
		}
	}

	public void onUnitHide(Unit unit) {
		// call all listeners
		for (UnitListener ls : listeners) {
			if (ls.getFilter().allow(unit))
				ls.onUnitHide(unit);
		}
	}

	public void onUnitCreate(Unit unit) {
		// call all listeners
		for (UnitListener ls : listeners) {
			if (ls.getFilter().allow(unit))
				ls.onUnitCreate(unit);
		}
	}

	public void onUnitDestroy(Unit unit) {
		// call all listeners
		for (UnitListener ls : listeners) {
			if (ls.getFilter().allow(unit))
				ls.onUnitDestroy(unit);
		}
	}

	public void onUnitMorph(Unit unit) {
		// call all listeners
		for (UnitListener ls : listeners) {
			if (ls.getFilter().allow(unit))
				ls.onUnitMorph(unit);
		}
	}

	public void onUnitRenegade(Unit unit) {
		// call all listeners
		for (UnitListener ls : listeners) {
			if (ls.getFilter().allow(unit))
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
	public void onUnitComplete(Unit unit) {
		// call all listeners
		for (UnitListener ls : listeners) {
			if (ls.getFilter().allow(unit))
				ls.onUnitComplete(unit);
		}
	}

	// FIXME implement these and add them to the UnitListener interface

	public void onUnitAttacked(Unit unit) {
		//
	}

	public static interface Filter{
		public abstract boolean allow(Unit unit);
	}
}
