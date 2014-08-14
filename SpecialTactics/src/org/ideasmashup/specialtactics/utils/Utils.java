package org.ideasmashup.specialtactics.utils;

import bwapi.Game;
import bwapi.Player;

public class Utils {

	private static Utils instance = null;

	private final Player player;
	private final Game game;


	protected Utils(Game game) {
		this.game = game;
		this.player = game.self();
	}

	public static Utils init(Game game) {
		if (instance == null) {
			instance = new Utils(game);
		}

		return instance;
	}

	public static Utils get() {
		return instance;
	}

	public Player getPlayer() {
		return player;
	}

	public Game getGame() {
		return game;
	}
}
