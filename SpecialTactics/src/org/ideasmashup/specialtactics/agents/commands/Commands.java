package org.ideasmashup.specialtactics.agents.commands;

import java.util.ArrayList;
import java.util.List;

import bwapi.Position;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitCommandType;
import bwapi.UnitType;
import bwapi.UpgradeType;

public class Commands {

	public static void assignToUnit(Unit bindee) {
		// assign a command to a Unit
	}

	public static void assignToGroup(Unit[] group) {
		// FIXME will need to create UnitsGroup class that can handle
		//      complex unit formations, but for now just use an array

		// unit array means that the order should be
	}

	public static enum States {
		PAUSED,
		RUNNING,
		WAITING_RESULT,
		FAILURE,
		SUCCESS,
		STOPPED,
		CANCELED,
	}

	public static enum NativeTypes {
		Attack_Move(UnitCommandType.Attack_Move),
		Attack_Unit(UnitCommandType.Attack_Unit),
		Build(UnitCommandType.Build),
		Build_Addon(UnitCommandType.Build_Addon),
		Train(UnitCommandType.Train),
		Morph(UnitCommandType.Morph),
		Research(UnitCommandType.Research),
		Upgrade(UnitCommandType.Upgrade),
		Set_Rally_Position(UnitCommandType.Set_Rally_Position),
		Set_Rally_Unit(UnitCommandType.Set_Rally_Unit),
		Move(UnitCommandType.Move),
		Patrol(UnitCommandType.Patrol),
		Hold_Position(UnitCommandType.Hold_Position),
		Stop(UnitCommandType.Stop),
		Follow(UnitCommandType.Follow),
		Gather(UnitCommandType.Gather),
		Return_Cargo(UnitCommandType.Return_Cargo),
		Repair(UnitCommandType.Repair),
		Burrow(UnitCommandType.Burrow),
		Unburrow(UnitCommandType.Unburrow),
		Cloak(UnitCommandType.Cloak),
		Decloak(UnitCommandType.Decloak),
		Siege(UnitCommandType.Siege),
		Unsiege(UnitCommandType.Unsiege),
		Lift(UnitCommandType.Lift),
		Land(UnitCommandType.Land),
		Load(UnitCommandType.Load),
		Unload(UnitCommandType.Unload),
		Unload_All(UnitCommandType.Unload_All),
		Unload_All_Position(UnitCommandType.Unload_All_Position),
		Right_Click_Position(UnitCommandType.Right_Click_Position),
		Right_Click_Unit(UnitCommandType.Right_Click_Unit),
		Halt_Construction(UnitCommandType.Halt_Construction),
		Cancel_Construction(UnitCommandType.Cancel_Construction),
		Cancel_Addon(UnitCommandType.Cancel_Addon),
		Cancel_Train(UnitCommandType.Cancel_Train),
		Cancel_Train_Slot(UnitCommandType.Cancel_Train_Slot),
		Cancel_Morph(UnitCommandType.Cancel_Morph),
		Cancel_Research(UnitCommandType.Cancel_Research),
		Cancel_Upgrade(UnitCommandType.Cancel_Upgrade),
		Use_Tech(UnitCommandType.Use_Tech),
		Use_Tech_Position(UnitCommandType.Use_Tech_Position),
		Use_Tech_Unit(UnitCommandType.Use_Tech_Unit),
		Place_COP(UnitCommandType.Place_COP),
		None(UnitCommandType.None),
		Unknown(UnitCommandType.Unknown);

		NativeTypes(UnitCommandType uct) {
			this.uct = uct;
		}

		private final UnitCommandType uct;

		public UnitCommandType getUnitCommandType() {
			return uct;
		}

		public static NativeTypes fromUnitCommandType(UnitCommandType uct) {
			for (NativeTypes nt : values()) {
				if (nt.uct == uct) {
					return nt;
				}
			}
			return null;
		}

		public boolean runCommand(Unit unit, Object... args) {
			boolean res = false;

			switch (this) {
				default:
					// not implemented yet !!
					System.err.println("NOT IMPLEMENTED YET : "+ this.name());
					break;
				case Attack_Move:
					// call function
					res = unit.attack((Position) args[0]);
					break;
				case Attack_Unit:
					// call function
					res = unit.attack((Unit) args[0]);
					break;
				case Build:
					// call function
					if (args.length == 1) {
						res = unit.build(unit.getTilePosition(), (UnitType) args[0]);
					}
					else if (args.length == 2) {
						res = unit.build((TilePosition) args[0], (UnitType) args[1]);
					}
					break;
				case Build_Addon:
					// call function
					res = unit.buildAddon((UnitType) args[0]);
					break;
				case Train:
					// call function
					res = unit.train((UnitType) args[0]);
					break;
				case Morph:
					// call function
					res = unit.morph((UnitType) args[0]);
					break;
				case Research:
					// call function
					res = unit.research((TechType) args[0]);
					break;
				case Upgrade:
					// call function
					res = unit.upgrade((UpgradeType) args[0]);
					break;
				case Set_Rally_Position:
					// call function
					res = unit.setRallyPoint((Position) args[0]);
					break;
				case Set_Rally_Unit:
					// call function
					res = unit.setRallyPoint((Unit) args[0]);
					break;
				case Move:
					// call function
					if (args.length == 1) {
						res = unit.move((Position) args[0]);
					}
					else if (args.length == 2) {
						res = unit.move((Position) args[0], (Boolean) args[1]);
					}
					break;
				case Patrol:
					// call function
					if (args.length == 1) {
						res = unit.patrol((Position) args[0]);
					}
					else if (args.length == 2) {
						res = unit.patrol((Position) args[0], (Boolean) args[1]);
					}
					break;
				case Hold_Position:
					// call function
					if (args.length == 0) {
						res = unit.holdPosition();
					}
					else if (args.length == 1) {
						res = unit.holdPosition((Boolean) args[0]);
					}
					break;
				case Stop:
					// call function
					// FIXME implement native function call
					if (args.length == 0) {
						res = unit.stop();
					}
					else if (args.length == 1) {
						res = unit.stop((Boolean) args[1]);
					}
					break;
				case Follow:
					// call function
					if (args.length == 1) {
						res = unit.follow((Unit) args[0]);
					}
					else if (args.length == 2) {
						res = unit.follow((Unit) args[0], (Boolean) args[1]);
					}
					break;
				case Gather:
					// call function
					if (args.length == 1) {
						res = unit.gather((Unit) args[0]);
					}
					else if (args.length == 2) {
						res = unit.gather((Unit) args[0], (Boolean) args[1]);
					}
					break;
				case Return_Cargo:
					// call function
					if (args.length == 0) {
						res = unit.returnCargo();
					}
					else if (args.length == 1) {
						res = unit.returnCargo((Boolean) args[1]);
					}
					break;
				case Repair:
					// call function
					// TODO implement smart-repairs with an agent that looks
					//      for repairable units within an area or group that
					//      can be repaired

					if (args.length == 1) {
						res = unit.repair((Unit) args[0]);
					}
					else if (args.length == 2) {
						res = unit.repair((Unit) args[0], (Boolean) args[1]);
					}
					break;
				case Burrow:
					// call function
					res = unit.burrow();
					break;
				case Unburrow:
					// call function
					res = unit.unburrow();
					break;
				case Cloak:
					// call function
					res = unit.cloak();
					break;
				case Decloak:
					// call function
					res = unit.decloak();
					break;
				case Siege:
					// call function
					res = unit.siege();
					break;
				case Unsiege:
					// call function
					res = unit.unsiege();
					break;
				case Lift:
					// call function
					res = unit.lift();
					break;
				case Land:
					// call function
					res = unit.land((TilePosition) args[0]);
					break;
				case Load:
					// call function
					if (args.length == 1) {
						res = unit.load((Unit) args[0]);
					}
					else if (args.length == 2) {
						res = unit.load((Unit) args[0], (Boolean) args[1]);
					}
					break;
				case Unload:
					// call function
					res = unit.unload((Unit) args[0]);
					break;
				case Unload_All:
					// call function
					res = unit.unloadAll();
					break;
				case Unload_All_Position:
					// call function
					if (args.length == 1) {
						res = unit.unloadAll((Position) args[0]);
					}
					else if (args.length == 2) {
						res = unit.unloadAll((Position) args[0], (Boolean) args[1]);
					}
					break;
				case Right_Click_Position:
					// call function
					if (args.length == 1) {
						res = unit.rightClick((Position) args[0]);
					}
					else if (args.length == 2) {
						res = unit.rightClick((Position) args[0], (Boolean) args[1]);
					}
					break;
				case Right_Click_Unit:
					// call function
					if (args.length == 1) {
						res = unit.rightClick((Unit) args[0]);
					}
					else if (args.length == 2) {
						res = unit.rightClick((Unit) args[0], (Boolean) args[1]);
					}
					break;
				case Halt_Construction:
					// call function
					res = unit.haltConstruction();
					break;
				case Cancel_Construction:
					// call function
					res = unit.cancelConstruction();
					break;
				case Cancel_Addon:
					// call function
					res = unit.cancelAddon();
					break;
				case Cancel_Train:
					// call function
					res = unit.cancelTrain();
					break;
				case Cancel_Train_Slot:
					// call function
					res = unit.cancelTrain((Integer) args[0]);
					break;
				case Cancel_Morph:
					// call function
					res = unit.cancelMorph();
					break;
				case Cancel_Research:
					// call function
					res = unit.cancelResearch();
					break;
				case Cancel_Upgrade:
					// call function
					res = unit.cancelUpgrade();
					break;
				case Use_Tech:
					// call function
					res = unit.useTech((TechType) args[0]);
					break;
				case Use_Tech_Position:
					// call function
					res = unit.useTech((TechType) args[0], (Position) args[1]);
					break;
				case Use_Tech_Unit:
					// call function
					res = unit.useTech((TechType) args[0], (Unit) args[1]);
					break;
				case Place_COP:
					// call function
					res = unit.placeCOP((TilePosition) args[0]);
					break;
				case None:
					// call function
					// This is a blank command : do nothing...
					break;
				case Unknown:
					// call function
					System.err.println("Command : UNKNOWN !");
					break;
			}

			return res;
		}
	}

	public static enum Types {
		ENDS_AT_POSITION(
			UnitCommandType.Attack_Move,
			UnitCommandType.Hold_Position,
			UnitCommandType.Move,
			UnitCommandType.Patrol,
			UnitCommandType.Return_Cargo,
			UnitCommandType.Right_Click_Position
		),
		ENDS_NEAR_UNIT(
			// special case because it never ends until the unit dies
			UnitCommandType.Follow
		),
		ENDS_AFTER_TRANSFORM(
			UnitCommandType.Unsiege,
			UnitCommandType.Siege,
			UnitCommandType.Lift,
			UnitCommandType.Land,
			UnitCommandType.Morph,
			UnitCommandType.Cloak,
			UnitCommandType.Decloak,
			UnitCommandType.Load,
			UnitCommandType.Unload,
			UnitCommandType.Research
		),
		ENDS_AFTER_TARGET_CHANGE(
			UnitCommandType.Repair,
			UnitCommandType.Gather
		),
		ENDS_AFTER_TARGET_DEATH(
			// special case because it never ends until the unit dies
			UnitCommandType.Follow
		),
		ENDS_NEVER(
			// special type for higher commands that only ends when they decide
			// it themselves (or AI decides it)
		);


		Types(UnitCommandType... types) {
			this.list = new ArrayList<UnitCommandType>(types.length);
			for (UnitCommandType type : types) {
				this.list.add(type);
			}
		}

		private final List<UnitCommandType> list;

		public static Types fromUnitCommandType(UnitCommandType type) {
			for (Types types : Types.values()) {
				if (types.list.contains(type)) {
					return types;
				}
			}
			return null;
		}
	}
}
