package org.ideasmashup.specialtactics;

import org.ideasmashup.specialtactics.brains.Brain;
import org.ideasmashup.specialtactics.brains.ProtossBrain;
import org.ideasmashup.specialtactics.brains.TerranBrain;
import org.ideasmashup.specialtactics.brains.ZergBrain;

import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;

public class AI {

	private Mirror mirror = new Mirror();

	private Game game;

	private Player self;
	
	private Brain brain;

	public void run() {
		mirror.getModule().setEventListener(new DefaultBWListener() {
			@Override
			public void onUnitCreate(Unit unit) {
				System.out.println("New unit " + unit.getType());
			}

			@Override
			public void onStart() {
				game = mirror.getGame();
				self = game.self();

				// Use BWTA to analyze map
				// This may take a few minutes if the map is processed first
				// time!
				System.out.println("Analyzing map...");
				BWTA.readMap();
				BWTA.analyze();
				System.out.println("Map data ready");

				// Initialize AI Brain
				if (self.getRace() == Race.Protoss) {
					brain = new ProtossBrain(game, self);
				}
				else if (self.getRace() == Race.Terran) {
					brain = new TerranBrain(game, self);
				}
				else if (self.getRace() == Race.Zerg) {
					brain = new ZergBrain(game, self);
				}
				else {
					System.out.println("Couldn't find brain for this race: "+ self.getRace());
					brain = null;
				}
			}

			@Override
			public void onFrame() {
				brain.onFrame();
			}
		});

		mirror.startGame();
	}

	public static void main(String... args) {
		new AI().run();
	}
}
