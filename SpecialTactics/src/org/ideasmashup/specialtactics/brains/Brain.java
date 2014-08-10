package org.ideasmashup.specialtactics.brains;

import java.util.ArrayList;
import java.util.List;

import org.ideasmashup.specialtactics.agents.Agent;
import org.ideasmashup.specialtactics.agents.Base;
import org.ideasmashup.specialtactics.agents.MineralPatch;
import org.ideasmashup.specialtactics.utils.UType;
import org.ideasmashup.specialtactics.utils.Utils;

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

	// low-level agents
	protected List<Agent> agents;

	// collection of all units
	protected Units units;

	public Brain(Mirror mirror) {
		this.mirror = mirror;

		agents = new ArrayList<Agent>();
	}

	public Brain(Game game) {
		this.game = game;
		this.self = game.self();

		this.units = new Units();
	}

	@Override
	public void onStart() {
		// TODO initialize pools, rquirements, operators, etc...

		this.game = mirror.getGame();
		this.self = game.self();

		// Use BWTA to analyze map
		// This may take a few minutes if the map is processed first
		// time!
		/*
		System.out.println("Analyzing map...");
		BWTA.readMap();
		BWTA.analyze();
		System.out.println("Map data ready");
		*/

		// Initialize all constraints and agents
		System.out.println("Starting by listing all visible units ");
		Agent agent;

		for (Unit unit : self.getUnits()) {
			System.out.println("Found "+ unit.getType());
			units.add(unit);

			//
			if (unit.getType() == UnitType.Resource_Mineral_Field) {
				System.out.println("  - is mineral patch : assigned MineralPatch agent");
				agent = new MineralPatch(unit);
				agents.add(agent);
			}

			//
			if (unit.getType() == Utils.get().getTypeFor(UType.BASE)) {
				System.out.println("  - is base center : assigne Base agent");
				agent = new Base(unit);
				agents.add(agent);
			}
		}
	}

	@Override
	public void onFrame() {

		for (Agent agent : agents) {
			// call all agents to do their low-level "stuff"
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

	}

	@Override
	public void onUnitEvade(Unit unit) {

	}

	@Override
	public void onUnitShow(Unit unit) {

	}

	@Override
	public void onUnitHide(Unit unit) {

	}

	@Override
	public void onUnitCreate(Unit unit) {
		// TODO refactor code so that this gets abck into AI.java
		//      and Brain handles more

		// add new unit to global Units pool
		units.add(unit);

		//
		//Agent needee = needs.findNeedeeFor(unit);
		//needee.fillNeed();
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		//
	}

	@Override
	public void onUnitMorph(Unit unit) {

	}

	@Override
	public void onUnitRenegade(Unit unit) {

	}

	@Override
	public void onSaveGame(String gameName) {

	}

	@Override
	public void onUnitComplete(Unit unit) {

	}

	@Override
	public void onPlayerDropped(Player player) {

	}
}
