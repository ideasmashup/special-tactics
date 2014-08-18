package org.ideasmashup.specialtactics.agents.commands;

import java.util.ArrayList;
import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitCommandType;

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

		public static NativeTypes fromUnitCommandType(UnitCommandType uct) {
			for (NativeTypes nt : values()) {
				if (nt.uct == uct) {
					return nt;
				}
			}
			return null;
		}

		public void runCommand(Unit unit, Object... args) {
			switch (this) {
				case Attack_Move:
					// call function
					unit.attack((Position) args[0]);
					break;
				case Attack_Unit:
					// call function
					break;
				case Build:
					// call function
					break;
				case Build_Addon:
					// call function
					break;
				case Train:
					// call function
					break;
				case Morph:
					// call function
					break;
				case Research:
					// call function
					break;
				case Upgrade:
					// call function
					break;
				case Set_Rally_Position:
					// call function
					break;
				case Set_Rally_Unit:
					// call function
					break;
				case Move:
					// call function
					break;
				case Patrol:
					// call function
					break;
				case Hold_Position:
					// call function
					break;
				case Stop:
					// call function
					break;
				case Follow:
					// call function
					break;
				case Gather:
					// call function
					break;
				case Return_Cargo:
					// call function
					break;
				case Repair:
					// call function
					break;
				case Burrow:
					// call function
					break;
				case Unburrow:
					// call function
					break;
				case Cloak:
					// call function
					break;
				case Decloak:
					// call function
					break;
				case Siege:
					// call function
					break;
				case Unsiege:
					// call function
					break;
				case Lift:
					// call function
					break;
				case Land:
					// call function
					break;
				case Load:
					// call function
					break;
				case Unload:
					// call function
					break;
				case Unload_All:
					// call function
					break;
				case Unload_All_Position:
					// call function
					break;
				case Right_Click_Position:
					// call function
					break;
				case Right_Click_Unit:
					// call function
					break;
				case Halt_Construction:
					// call function
					break;
				case Cancel_Construction:
					// call function
					break;
				case Cancel_Addon:
					// call function
					break;
				case Cancel_Train:
					// call function
					break;
				case Cancel_Train_Slot:
					// call function
					break;
				case Cancel_Morph:
					// call function
					break;
				case Cancel_Research:
					// call function
					break;
				case Cancel_Upgrade:
					// call function
					break;
				case Use_Tech:
					// call function
					break;
				case Use_Tech_Position:
					// call function
					break;
				case Use_Tech_Unit:
					// call function
					break;
				case Place_COP:
					// call function
					break;
				case None:
					// call function
					break;
				case Unknown:
					// call function
					break;
			}
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
