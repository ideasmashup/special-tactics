package org.ideasmashup.specialtactics.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.agents.DefaultAgent;
import org.ideasmashup.specialtactics.tiles.Tile;

import bwapi.Color;
import bwapi.Game;
import bwapi.TilePosition;


public class Tiles extends DefaultAgent {

	private static final String PERSISTENT_FILENAME = "tiles-persistent.grid";
	private static Tiles instance = null;

	private final String path;


	private Tile[][] tiles;
	private int columns, rows;


	private Tiles() {
		super();

		this.path = "data/tiles/" + AI.getGame().mapHash();
		loadTiles();

		// FIXME debug code remove when no longer needed
		Agents.getInstance().add(this);
	}

	@Override
	public void update() {
		super.update();

		// FIXME debug code remove when no longer needed
		// draw the map grid onto the game
		if (tiles != null) {
			for (int x = 0; x < columns; x++) {
				AI.getGame().drawLineMap(x * Tile.WIDTH, 0, x * Tile.WIDTH, rows * Tile.HEIGHT, Color.Black);
			}
			for (int y = 0; y < rows; y++) {
				AI.getGame().drawLineMap(0, y * Tile.HEIGHT, columns * Tile.WIDTH, y * Tile.HEIGHT, Color.Black);
			}
		}
	}

	private void loadTiles() {
		// load tiles data from file
		/*
			The file data will first be hard-coded by humans(?) then it will
			become data generated by the AI upon analysis of multiple games
			against AIs and players to optimize buildings locations and other
			stuff (threat zones, defensive positions, safe paths, etc...)
		 */
		try {
			// load persistant tiles data
			String filename = path + "/" + PERSISTENT_FILENAME;
			File file = new File(filename);
			if (!file.exists()) {
				// file doesn't exist, create folders to allow file creation
				if (!file.getParentFile().mkdirs()) {
					System.err.println("Couldn't create folder structure for file : "+ file.getCanonicalPath());
				}
				if (file.createNewFile()) {
					System.err.println("Couldn't create new file : "+ file.getCanonicalPath());
				}
			}

			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream in = new ObjectInputStream(fis);
			this.tiles = (Tile[][]) in.readObject();
			this.columns = this.tiles[0].length;
			this.rows = this.tiles.length;
			in.close();

		}
		catch (Exception e) {
			e.printStackTrace();

			// error when loading file : reset to blank tiles
			this.rows = AI.getGame().mapHeight() * (Tile.HEIGHT / 32);  // bwapi standard resolution is 32px
			this.columns = AI.getGame().mapWidth() * (Tile.WIDTH / 32); // bwapi standard resolution is 32px
			this.tiles = new Tile[rows][columns];

			for (int row = 0; row < this.rows; row++) {
				for (int column = 0; column < this.columns; column++) {
					this.tiles[row][column] = new Tile();
				}
			}
			System.err.println(" - Reset Tiles[][] to default blank tiles");
		}
		finally {
			// set tiles with their position and grid for position awareness
			for (int row = 0; row < this.rows; row++) {
				for (int column = 0; column < this.columns; column++) {
					this.tiles[row][column].setGrid(this.tiles);
					this.tiles[row][column].setTilePosition(row, column);
				}
			}
		}
	}

	public void saveTiles() {
		// save tiles data to file
		try {
			// if no persistent file then save as persistent data
			// otherwise save as {opponent_name}-{matchup}-{datetime}.grid

			Game game = AI.getGame();
			String filename = path + "/" + PERSISTENT_FILENAME;
			File file = new File(filename);

			if (!file.exists()) {
				// file doesn't exist, create folders to allow file creation
				if (!file.getParentFile().mkdirs()) {
					System.err.println("Couldn't create folder structure for file : "+ file.getCanonicalPath());
				}
				if (file.createNewFile()) {
					System.err.println("Couldn't create new file : "+ file.getCanonicalPath());
				}
			}
			else {
				// already have a persistent file, store this game's tiles to a new file
				filename = path + "/" + game.enemy().getName()
						+ "-" + AI.getPlayer().getRace().toString().charAt(0)
						+ "v" + game.enemy().getRace().toString().charAt(0)
						+ "-" + new Date()
						+ "-.grid";
			}

			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(tiles);
			out.flush();
			out.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Tiles getInstance() {
		if (instance == null) {
			instance = new Tiles();
		}
		return instance;
	}

	public Tile getTile(TilePosition tp) {
		// highlight this tile
		AI.getGame().drawBoxMap(tp.getX() * Tile.WIDTH + 1, tp.getY() * Tile.HEIGHT + 1, (tp.getX() + 1) * Tile.WIDTH - 1, (tp.getY() + 1) * Tile.HEIGHT - 1, Color.Purple, false);
		return tiles[tp.getY()][tp.getX()];
	}

	public static enum Trails {
		TRAIL_MINING,      // float : trail sum by workers passing on tile
		TRAIL_MOVING,      // float : trail sum by units moving on tile
		TRAIL_ATTACKING,   // float : trail sum by units attacking on tile
		TRAIL_FLEEING_LD,  // float : trail sum by units fleeing local-damage on tile
		TRAIL_FLEEING_SD,  // float : trail sum by units fleeing splash-damage on tile
		TRAIL_SAFE,        // float : trail sum by units passing on tile untouched
		TRAIL_UNSEEN,      // float : trail sum by units not seen on this tile
	}

	public static enum Specs {
		ATTACK_COST,       // float : attack costs to attack tile
		ATTACK_BONUS,      // float : attack bonus given when attacking tile
		DEFENSE_COST,      // float : cost to defend tile
		DEFENSE_BONUS,     // float : defense bonus given on tile
		DAMAGE_COST,       // float : estimate of DPS taken on tile
		BUILDABLE,         // boolean : can build on tile or not ?
		BUILD_TYPES,       // UnitType[] : preferred buildings to build on tile
		BUILD_DIRECTIONS,  // String[] : top, left, bottom, right, edge, center
	}
}
