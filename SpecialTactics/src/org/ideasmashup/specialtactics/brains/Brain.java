package org.ideasmashup.specialtactics.brains;

import java.util.ArrayList;
import java.util.List;

import org.ideasmashup.specialtactics.agents.Agent;
import org.ideasmashup.specialtactics.agents.Base;
import org.ideasmashup.specialtactics.agents.MineralPatch;
import org.ideasmashup.specialtactics.agents.Scout;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Supplies;
import org.ideasmashup.specialtactics.managers.Units;

import bwapi.BWEventListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;

public class Brain implements BWEventListener {

	protected Mirror mirror;
	protected Game game;
	protected Player self;

	// internal flags
	protected int frames = 0;
	protected int prevMinerals = 0;
	protected int prevGas = 0;
	protected int prevSupply = 0;

	// low-level agents
	protected List<Agent> agents;

	public Brain(Game game) {
		this.game = game;
		this.self = game.self();

		this.agents = new ArrayList<Agent>();

		// must initialize managers in correct order
		Units.init();
		Resources.init();
		Supplies.init();

		// needs manager always last
		Needs.init();

		// creates scouting agent
		final Scout scout = new Scout(null);
		Units.addListener(scout);
		agents.add(scout);
	}

	@Override
	public void onStart() {
		// FIXME DO NOT USE!!! NOT CALLED YET IN THIS VERSION!!!

		// TODO initialize pools, rquirements, operators, etc...


		// Use BWTA to analyze map
		// This may take a few minutes if the map is processed first
		// time!
		/*
		System.out.println("Analyzing map...");
		BWTA.readMap();
		BWTA.analyze();
		System.out.println("Map data ready");
		*/
	}

	@Override
	public void onFrame() {

		// Low priority code running every 20 frames instead of on every frame
		// https://code.google.com/p/bwapi/wiki/StarcraftGuide#What_is_Starcraft%27s_frame_rate?

		if (++frames % 20 == 0) {
			frames = 0;

			int curGas = self.gas();
			int curMinerals = self.minerals();
			int curSupply = self.supplyTotal() - self.supplyUsed();

			if (prevMinerals != curMinerals || prevGas != curGas) {
				prevGas = curGas;
				prevMinerals = curMinerals;

				Resources.onResourcesChange(curMinerals, curGas);
			}

			if (prevSupply != curSupply) {
				prevSupply = curSupply;

				Supplies.onSupplyChange(curSupply);
			}
		}

		// High priority code running on every frame for intensive micro
		// this includes running all agents update()

		for (Agent agent : agents) {
			agent.update();
		}

		// TODO call all ops and high level classes to do strategic/global stuff
	}

	@Override
	public void onEnd(boolean isWinner) {
		if (isWinner) {
			//
		}
		else {
			//
		}
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
		System.out.println("unit #"+ unit.getID() + "("+ unit.getType() +") discovered");
		Units.onUnitDiscover(unit);
	}

	@Override
	public void onUnitEvade(Unit unit) {
		System.out.println("unit #"+ unit.getID() + "("+ unit.getType() +") evaded");
		Units.onUnitEvade(unit);
	}

	@Override
	public void onUnitShow(Unit unit) {
		System.out.println("unit #"+ unit.getID() + "("+ unit.getType() +") shown");
		Units.onUnitShow(unit);
	}

	@Override
	public void onUnitHide(Unit unit) {
		System.out.println("unit #"+ unit.getID() + "("+ unit.getType() +") hidden");
		Units.onUnitHide(unit);
	}

	@Override
	public void onUnitCreate(Unit unit) {
		// add new unit to global Units pool
		System.out.println("unit #"+ unit.getID() + "("+ unit.getType() +") created");

		Agent agent;

		//
		if (unit.getType() == UnitType.Resource_Mineral_Field && unit.isVisible()) {
			System.out.println("  - is mineral patch : assigned MineralPatch agent");
			agent = new MineralPatch(unit);
			agents.add(agent);
		}

		//
		if (unit.getType() == Units.Types.BUILDING_BASE.getUnitType()) {
			System.out.println("  - is base center : assigne Base agent");
			agent = new Base(unit);
			agents.add(agent);
		}

		// TODO verify that call order is correct for new Agents that implement
		//      UnitListener and may be called after/before? they are created

		Units.add(unit);
		Units.onUnitCreate(unit);
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		System.out.println("unit #"+ unit.getID() + "("+ unit.getType() +") destroyed");
		Units.onUnitDestroy(unit);
	}

	@Override
	public void onUnitMorph(Unit unit) {
		System.out.println("unit #"+ unit.getID() + "("+ unit.getType() +") morphed");
		Units.onUnitMorph(unit);
	}

	@Override
	public void onUnitRenegade(Unit unit) {
		System.out.println("unit #"+ unit.getID() + "("+ unit.getType() +") renegade");
		Units.onUnitRenegade(unit);
	}

	@Override
	public void onSaveGame(String gameName) {

	}

	@Override
	public void onUnitComplete(Unit unit) {
		System.out.println("unit #"+ unit.getID() + "("+ unit.getType() +") completed");
		Units.onUnitComplete(unit);
	}

	@Override
	public void onPlayerDropped(Player player) {

	}
}
