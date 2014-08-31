package org.ideasmashup.specialtactics;

import org.ideasmashup.specialtactics.brains.Brain;
import org.ideasmashup.specialtactics.brains.ProtossBrain;
import org.ideasmashup.specialtactics.brains.SpectatorBrain;
import org.ideasmashup.specialtactics.brains.TerranBrain;
import org.ideasmashup.specialtactics.brains.ZergBrain;

import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Race;

public class AI {

	private static final Mirror mirror = new Mirror();

	private static AI ai;

	private static Game game;

	private static Player self;

	private static Brain brain;

	private static GUI gui;

	public void run() {
		mirror.getModule().setEventListener(new DefaultBWListener() {
			@Override
			public void onStart() {
				game = mirror.getGame();
				self = game.self();

				// Initialize AI Brain
				if (self.getRace() == Race.Protoss) {
					brain = new ProtossBrain(game);
				}
				else if (self.getRace() == Race.Terran) {
					brain = new TerranBrain(game);
				}
				else if (self.getRace() == Race.Zerg) {
					brain = new ZergBrain(game);
				}
				else {
					System.out.println("Couldn't find brain for this race: "+ self.getRace());
					brain = new SpectatorBrain(game);
					System.out.println("AI initialized in 'Spectator mode'");
				}

				// launch Swing + Processing GUI
				try {
					gui = new GUI(ai, brain);
					gui.setVisible(true);
				}
				catch(Exception e) {
					e.printStackTrace();
				}

				// force call brain.onStart()
				brain.onStart();

				// reaffect listener to specialized brain
				mirror.getModule().setEventListener(brain);
			}
		});

		mirror.startGame();
	}

	public static Game getGame() {
		return game;
	}

	public static Player getPlayer() {
		return self;
	}

	public static void say(String message) {
		game.sendText(message);
	}

	public static void main(String... args) {
		ai = new AI();
		ai.run();
	}
}
