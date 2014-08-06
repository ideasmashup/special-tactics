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

public class Brain extends DefaultBWListener {

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
		game.setTextSize(10);
		game.drawTextScreen(10, 10, "Playing as " + self.getName()
				+ " - " + self.getRace());

		StringBuilder units = new StringBuilder("My units:\n");

		// iterate through my units
		for (Unit myUnit : self.getUnits()) {
			units.append(myUnit.getType()).append(" ")
					.append(myUnit.getTilePosition()).append("\n");

			// if there's enough minerals, train an SCV
			if (myUnit.getType() == UnitType.Terran_Command_Center
					&& self.minerals() >= 50) {
				myUnit.train(UnitType.Terran_SCV);
			}

			// if it's a drone and it's idle, send it to the closest
			// mineral patch
			if (myUnit.getType().isWorker() && myUnit.isIdle()) {
				Unit closestMineral = null;

				// find the closest mineral
				for (Unit neutralUnit : game.neutral().getUnits()) {
					if (neutralUnit.getType().isMineralField()) {
						if (closestMineral == null
								|| myUnit.getDistance(neutralUnit) < myUnit
										.getDistance(closestMineral)) {
							closestMineral = neutralUnit;
						}
					}
				}

				// if a mineral patch was found, send the drone to
				// gather it
				if (closestMineral != null) {
					myUnit.gather(closestMineral, false);
				}
			}
		}

		// draw my units on screen
		game.drawTextScreen(10, 25, units.toString());
	}
}
