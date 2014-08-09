package org.ideasmashup.specialtactics.brains;

import java.util.List;
import java.util.ArrayList;

import org.ideasmashup.specialtactics.agents.Bindable;
import org.ideasmashup.specialtactics.agents.MineralPatch;

import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

public class Brain implements BWEventListener {

	protected Mirror mirror;
	protected Game game;
	protected Player self;

	protected List<Bindable> agents;

	public Brain(Mirror mirror) {
		this.mirror = mirror;

		agents = new ArrayList<Bindable>();
	}

	public Brain(Game game) {
		this.game = game;
		this.self = game.self();
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

		for (Unit unit : self.getUnits()) {
			System.out.println("Found "+ unit.getType());

			//
			if (unit.getType() == UnitType.Resource_Mineral_Field) {
				System.out.println("  - is mineral patch : assigned MineralPatch agent");
				agents.add(new MineralPatch(unit));
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
		Agent needee = needs.findNeedeeFor(unit);
		needee.fillNeed();
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
