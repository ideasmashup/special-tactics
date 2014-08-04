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
					brain = new ProtossBrain();
				}
				else if (self.getRace() == Race.Terran) {
					brain = new TerranBrain();
				}
				else if (self.getRace() == Race.Zerg) {
					brain = new ZergBrain();
				}
				else {
					System.out.println("Couldn't find brain for this race: "+ self.getRace());
					brain = null;
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
		});

		mirror.startGame();
	}

	public static void main(String... args) {
		new AI().run();
	}
}
