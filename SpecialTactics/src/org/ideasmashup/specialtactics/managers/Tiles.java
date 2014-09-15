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
import org.ideasmashup.specialtactics.brains.BrainListener;
import org.ideasmashup.specialtactics.tiles.Tile;

import bwapi.Color;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.TilePosition;


public class Tiles extends DefaultAgent implements BrainListener {

	private static final String PERSISTENT_FILENAME = "tiles-persistent.grid";
	private static Tiles instance = null;

	private final String path;

	// editor variables
	private Mode mode;
	private enum Mode{
		build,
		units,
		debug,
		view
	}

	private Tile[][] gridBuild; // structure tiles (general)
	private Tile[][] gridUnits; // units tiles (trails)

	private Tiles() {
		super();

		this.path = "data/tiles/" + AI.getGame().mapHash();
		loadTiles();

		// editor parameters
		this.mode = Mode.build;

		// FIXME debug code remove when no longer needed
		Agents.getInstance().add(this);
	}

	@Override
	public void update() {
		super.update();

		// FIXME debug code remove when no longer needed
		// draw the map grid onto the game
		if (mode == Mode.view) {
			if (gridUnits != null) {
				for (int x = 0; x < gridUnits[0].length; x++) {
					AI.getGame().drawLineMap(x * Tile.SIZE_UNIT, 0, x * Tile.SIZE_UNIT, gridUnits[0].length * Tile.SIZE_UNIT, Color.Black);
				}
				for (int y = 0; y < gridUnits.length; y++) {
					AI.getGame().drawLineMap(0, y * Tile.SIZE_UNIT, gridUnits.length * Tile.SIZE_UNIT, y * Tile.SIZE_UNIT, Color.Black);
				}
			}
			else if (gridBuild != null) {
				for (int x = 0; x < gridBuild[0].length; x++) {
					AI.getGame().drawLineMap(x * Tile.SIZE_BUILD, 0, x * Tile.SIZE_BUILD, gridBuild[0].length * Tile.SIZE_BUILD, Color.Black);
				}
				for (int y = 0; y < gridBuild.length; y++) {
					AI.getGame().drawLineMap(0, y * Tile.SIZE_BUILD, gridBuild.length * Tile.SIZE_BUILD, y * Tile.SIZE_BUILD, Color.Black);
				}
			}
		}
		else if (mode == Mode.build) {
			// draw grid : green = buildable, blue = powered, red = unwalkable, unbuildable
			if (gridBuild != null) {
				for (int x = 0; x < gridBuild[0].length; x++) {
					AI.getGame().drawLineMap(x * Tile.SIZE_BUILD, 0, x * Tile.SIZE_BUILD, gridBuild[0].length * Tile.SIZE_BUILD, Color.Black);
				}
				for (int y = 0; y < gridBuild.length; y++) {
					AI.getGame().drawLineMap(0, y * Tile.SIZE_BUILD, gridBuild.length * Tile.SIZE_BUILD, y * Tile.SIZE_BUILD, Color.Black);
				}
			}
		}
		else if (mode == Mode.units) {
			// draw grid : purple = trail > 0
			if (gridUnits != null) {
				for (int x = 0; x < gridUnits[0].length; x++) {
					AI.getGame().drawLineMap(x * Tile.SIZE_UNIT, 0, x * Tile.SIZE_UNIT, gridUnits[0].length * Tile.SIZE_UNIT, Color.Black);
				}
				for (int y = 0; y < gridUnits.length; y++) {
					AI.getGame().drawLineMap(0, y * Tile.SIZE_UNIT, gridUnits.length * Tile.SIZE_UNIT, y * Tile.SIZE_UNIT, Color.Black);
				}
			}
		}
		else if (mode == Mode.debug) {
			// do something, like display infos of MousePosition tile(s)
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
			this.gridBuild = (Tile[][]) in.readObject();
			this.gridUnits = (Tile[][]) in.readObject();
			in.close();

			// set tiles with their position and grid for position awareness
			for (int row = 0; row < this.gridBuild.length; row++) {
				for (int column = 0; column < this.gridBuild[0].length; column++) {
					this.gridBuild[row][column].setGrid(gridBuild);
					this.gridBuild[row][column].setTilePosition(row, column);
				}
			}
			for (int row = 0; row < this.gridUnits.length; row++) {
				for (int column = 0; column < this.gridUnits[0].length; column++) {
					this.gridUnits[row][column].setGrid(gridUnits);
					this.gridUnits[row][column].setTilePosition(row, column);
				}
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tiles : cannot load tiles file...");

			// error when loading file : reset to blank tiles
			Game game = AI.getGame();
			int UNITS_BUILD_RATIO = Tile.SIZE_BUILD / Tile.SIZE_UNIT;

			this.gridBuild = new Tile[game.mapHeight()][game.mapWidth()];
			this.gridUnits = new Tile[game.mapHeight() * UNITS_BUILD_RATIO][game.mapWidth() * UNITS_BUILD_RATIO];

			System.err.println("Tiles (buildings) : "+ game.mapHeight() +"x"+ game.mapWidth());
			System.err.println("Tiles (units) : "+ game.mapHeight() * UNITS_BUILD_RATIO +"x"+ game.mapWidth() * UNITS_BUILD_RATIO);

			System.err.println("Tiles : looping through map tiles to initialize grid");

			// fill default tiles
			Tile tileUnits, tileBuild;
			boolean buildable, walkable;

			for (int row = 0; row < this.gridBuild.length; row++) {
				for (int column = 0; column < this.gridBuild[0].length; column++) {
					// detect specs using Game infos
					buildable = game.isBuildable(column, row);
					walkable = game.isWalkable(column, row);

					// buildings tile first
					tileBuild = new Tile(Tile.SIZE_BUILD);

					tileBuild.setGrid(gridBuild);
					tileBuild.setTilePosition(row, column);

					tileBuild.setSpecs(Specs.BUILDABLE, buildable);
					tileBuild.setSpecs(Specs.WALKABLE, walkable);

					this.gridBuild[row][column] = tileBuild;

					// apply building specs to sub-tiles (unit tiles)
					for (int y = 0; y < UNITS_BUILD_RATIO; y++) {
						for (int x = 0; x < UNITS_BUILD_RATIO; x++) {
							tileUnits = new Tile(Tile.SIZE_UNIT);

							tileUnits.setGrid(gridUnits);
							tileUnits.setTilePosition(row * UNITS_BUILD_RATIO + y, column * UNITS_BUILD_RATIO + x);

							tileBuild.setSpecs(Specs.BUILDABLE, buildable);
							tileBuild.setSpecs(Specs.WALKABLE, walkable);

							this.gridUnits[row * UNITS_BUILD_RATIO + y][column * UNITS_BUILD_RATIO + x] = tileUnits;
						}
					}
				}
			}

			System.err.println("Tiles : done !");
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
			out.writeObject(gridBuild);
			out.writeObject(gridUnits);
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

	public Tile getBuildTile(Position p) {
		TilePosition tp = new TilePosition(p.getX() / Tile.SIZE_BUILD, p.getY() / Tile.SIZE_BUILD);
		Tile tile = gridBuild[tp.getY()][tp.getX()];

		// highlight this tile?
		colorTile(tile, Color.Purple);

		return tile;
	}

	public Tile getUnitTile(Position p) {
		TilePosition tp = new TilePosition(p.getX() / Tile.SIZE_UNIT, p.getY() / Tile.SIZE_UNIT);
		Tile tile = gridUnits[tp.getY()][tp.getX()];

		// highlight this tile?
		colorTile(tile, Color.Purple);

		return tile;
	}

	public void colorTile(Tile tile, Color color, int padding, boolean fill) {
		TilePosition tp = tile.getTilePosition();
		int size = tile.getSize();
		AI.getGame().drawBoxMap(tp.getX() * size + padding, tp.getY() * size + padding, (tp.getX() + 1) * size - padding, (tp.getY()+ 1) * size - padding, color, fill);
	}

	public void colorTile(Tile tile, Color color) {
		colorTile(tile, color, 1, false);
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
		WALKABLE,          // boolean : can walk on tile or not ?
		BUILDABLE,         // boolean : can build on tile or not ?
		BUILD_TYPES,       // UnitType[] : preferred buildings to build on tile
		BUILD_DIRECTIONS,  // String[] : top, left, bottom, right, edge, center
	}

	@Override
	public void onSendText(String text) {
		//
	}

	@Override
	public void onReceiveText(Player player, String text) {
		if (player == AI.getPlayer()) {
			if (text.startsWith("/tiles")) {
				String[] cmd = text.split(" ");
				if (cmd.length > 1) {
					switch (cmd[1]) {
						default:
							boolean found = false;
							for (Mode m : Mode.values()) {
								if (m.toString().equals(cmd[1])) {
									AI.say("Tiles : set to mode "+ m);
									this.mode = m;
									found = true;
									break;
								}
							}
							if (!found) {
								AI.say("Tiles : couldn't find mode : "+ cmd[1]);
							}
							break;
						case "select":
							AI.getGame().getMouseState(0);
							break;
					}
				}
			}
		}
		else if (text.startsWith("/b")) {

		}
	}
}
