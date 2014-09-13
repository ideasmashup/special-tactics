package org.ideasmashup.specialtactics.brains;

import java.util.LinkedList;
import java.util.List;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.agents.Agent;
import org.ideasmashup.specialtactics.agents.AsyncOrders;
import org.ideasmashup.specialtactics.agents.Base;
import org.ideasmashup.specialtactics.agents.Clock;
import org.ideasmashup.specialtactics.agents.ExperimentalProduction;
import org.ideasmashup.specialtactics.agents.MineralPatch;
import org.ideasmashup.specialtactics.agents.UnitAgent;
import org.ideasmashup.specialtactics.agents.scouts.FindEnemyMain;
import org.ideasmashup.specialtactics.managers.Agents;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Supplies;
import org.ideasmashup.specialtactics.managers.Tiles;
import org.ideasmashup.specialtactics.managers.Units;

import bwapi.BWEventListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;

public class Brain implements BWEventListener {

	protected static Brain instance;

	// BWAPI classes
	protected Mirror mirror;
	protected Game game;
	protected Player self;

	// MANAGER classes
	protected Agents agents;
	protected AsyncOrders orders;
	protected Units units;
	protected Resources resources;
	protected Supplies supplies;
	protected Needs needs;

	// internal flags
	protected long frames = 0;
	protected int prevMinerals = 0;
	protected int prevGas = 0;
	protected int prevSupply = 0;
	protected BrainListener listener;

	public Brain(Game game) {
		instance = this;

		this.game = game;
		this.self = game.self();
		this.listener = null;

		// must initialize managers in correct order
		agents = Agents.getInstance();
		orders = AsyncOrders.getInstance();
		units = Units.getInstance();
		resources = Resources.getInstance();
		supplies = Supplies.getInstance();

		// needs manager always last
		needs = Needs.getInstance();

		// creates timer agent
		final Clock clock = new Clock();
		agents.add(clock);

		// creates scouting agent
//		final Scout scout = new Scout(null);
//		units.addListener(scout);
//		agents.add(scout);
		final FindEnemyMain scout = new FindEnemyMain(null);
		agents.add(scout);

	}

	public static Brain get() {
		return instance;
	}

	public void setListener(BrainListener ls) {
		this.listener = ls;
	}

	public void removeListener(BrainListener ls) {
		this.listener = null;
	}

	@Override
	public void onStart() {

		// Use BWTA to analyze map
		// This may take a few minutes if the map is processed first
		// time!
		System.out.println("Analyzing map...");
		BWTA.readMap();
		BWTA.analyze();
		System.out.println("Map data ready");

		// TODO initialize pools, requirements, operators, etc...

		/*
			- after BWTA tag strategic areas tiles
				- tag minerals front (mining zones)
				- tag minerals back (drop zones)
				- tag choke
				- tag base outline (drop defense)
		*/
	}

	@Override
	public void onFrame() {
		if(game.isPaused()) return;

		// Low priority code running every 20 frames instead of on every frame
		// https://code.google.com/p/bwapi/wiki/StarcraftGuide#What_is_Starcraft%27s_frame_rate?

		try {
			//
			if (frames++ == 0) {
				AI.say("gl hf");
				AI.say("HINT: to speed up game type /speed 0");

				// FIXME convert this agent into a manager when possible
				//       currently an agent for quick prototyping-testing
				Agent agent = new ExperimentalProduction();
				agents.add(agent);

				// FIXME grid drawing for debugging! (remove Agent impl. later)
				Tiles.getInstance();
			}

			if (frames % 18 == 0) {
				int curGas = self.gas();
				int curMinerals = self.minerals();
				int curSupply = self.supplyTotal() - self.supplyUsed();

				if (prevMinerals != curMinerals || prevGas != curGas) {
					prevGas = curGas;
					prevMinerals = curMinerals;

					resources.onResourcesChange(curMinerals, curGas);
				}

				if (prevSupply != curSupply) {
					prevSupply = curSupply;

					supplies.onSupplyChange(curSupply);
				}
			}

			// High priority code running on every frame for intensive micro
			// this includes running all agents update()

			List<Agent> zombies = new LinkedList<Agent>();
			List<Agent> list = agents.getList();

			for (Agent agent : list) {
				// update living agents and burn - previously on AMC's - the walking dead
				if (agent.isDestroyed()) {
					zombies.add(agent);
				}
				else {
					agent.update();
				}
			}

			// cleanup zombies
			for (Agent zombie : zombies) {
				agents.remove(zombie);
			}

			// TODO call all ops and high-level classes to do
			//      strategic | global stuff
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEnd(boolean isWinner) {
		if (isWinner) {
			//
		}
		else {
			//
		}

		// close AI and save game data
		terminate();
	}

	@Override
	public void onSendText(String text) {

	}

	@Override
	public void onReceiveText(Player player, String text) {

	}

	@Override
	public void onPlayerLeft(Player player) {

	}

	@Override
	public void onNukeDetect(Position target) {

	}

	@Override
	public void onUnitDiscover(Unit unit) {
		units.add(unit);
		units.onUnitDiscover(unit);
	}

	@Override
	public void onUnitEvade(Unit unit) {
		units.onUnitEvade(unit);
	}

	@Override
	public void onUnitShow(Unit unit) {
		units.onUnitShow(unit);
	}

	@Override
	public void onUnitHide(Unit unit) {
		units.onUnitHide(unit);
	}

	@Override
	public void onUnitCreate(Unit unit) {
		// add new unit to global Units pool
		System.out.println("unit #"+ unit.getID() + " ("+ unit.getType() +") created");

		UnitAgent agent;

		//
		if (unit.getType() == UnitType.Resource_Mineral_Field && unit.isVisible()) {
			System.out.println("  - is mineral patch : assigned MineralPatch agent");
			agent = new MineralPatch(unit);
			agents.add(agent);
		}

		//
		if (unit.getType() == Units.Types.BASE.getUnitType()) {
			System.out.println("  - is base center : assigne Base agent");
			agent = new Base(unit);
			agents.add(agent);
		}

		// TODO verify that call order is correct for new Agents that implement
		//      UnitListener and may be called after/before? they are created

//		units.add(unit);
		units.onUnitCreate(unit);
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		units.onUnitDestroy(unit);
	}

	@Override
	public void onUnitMorph(Unit unit) {
		units.onUnitMorph(unit);
	}

	@Override
	public void onUnitRenegade(Unit unit) {
		units.onUnitRenegade(unit);
	}

	@Override
	public void onSaveGame(String gameName) {

	}

	@Override
	public void onUnitComplete(Unit unit) {
		units.onUnitComplete(unit);
	}

	@Override
	public void onPlayerDropped(Player player) {

	}

	private void terminate() {
		// terminate AI and save all AI/game data
		Tiles.getInstance().saveTiles();
		AI.terminate(0);
	}
}
