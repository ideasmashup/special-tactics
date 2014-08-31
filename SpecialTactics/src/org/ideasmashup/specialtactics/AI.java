package org.ideasmashup.specialtactics;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

	private static final boolean SHOW_GUI = true;

	private static final String TASKLIST = "tasklist";
	private static final String KILL = "taskkill /IM ";
	private static final String GAME_PROCESS = "StarCraft.exe";

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
				if (SHOW_GUI) {
					try {
						gui = new GUI(brain);
						gui.show(true);
					}
					catch(Exception e) {
						e.printStackTrace();
						terminate(-1);
					}
				}
				else {
					System.err.println("GUI disabled to save cpu and memory. Enable in AI.java by setting SHOW_GUI = true");
				}

				// force call brain.onStart()
				brain.onStart();

				// reaffect listener to specialized brain
				mirror.getModule().setEventListener(brain);
			}
		});

		mirror.startGame();
	}

	public static void terminate(int exitcode) {
		// kill starcraft.exe process
		try {
			if (isProcessRunning(GAME_PROCESS)) {
				killProcess(GAME_PROCESS);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		// kill bot
		System.exit(exitcode);
	}

	private static boolean isProcessRunning(String serviceName) throws Exception {
		Process p = Runtime.getRuntime().exec(TASKLIST);
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			//System.out.println(line);
			if (line.contains(serviceName)) {
				return true;
			}
		}

		return false;
	}

	private static void killProcess(String serviceName) throws Exception {
		Runtime.getRuntime().exec(KILL + serviceName);
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
