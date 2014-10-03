package org.ideasmashup.specialtactics.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.agents.DefaultAgent;
import org.ideasmashup.specialtactics.brains.BrainListener;
import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.managers.Units.Filter;
import org.ideasmashup.specialtactics.tiles.Tile;

import bwapi.Color;
import bwapi.Game;
import bwapi.Player;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;


public class Tiles extends DefaultAgent implements BrainListener, UnitListener {

	private static final String PERSISTENT_FILENAME = "tiles-persistent.grid";
	private static Tiles instance = null;

	private final String path;

	// editor variables
	private Mode mode;
	public enum Mode{
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
		this.mode = Mode.view;

		Units.getInstance().addListener(this);

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

				// show buildable
				for (int x = 0; x < gridBuild[0].length; x++) {
					for (int y = 0; y < gridBuild.length; y++) {
						Tile tile = gridBuild[y][x];
						if (((Boolean) tile.getSpecs(Specs.BUILDABLE)).booleanValue()) {
							//colorTile(tile, Color.Green);
						}
						else {
							colorTile(tile, Color.Red);
						}
					}
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

				// show walkable
				Tile tile;
				for (int x = 0; x < gridUnits[0].length; x++) {
					for (int y = 0; y < gridUnits.length; y++) {
						tile = gridUnits[y][x];

						if (AI.getGame().isWalkable(x, y)) {//((Boolean) tile.getSpecs(Specs.WALKABLE)).booleanValue()) {
							//colorTile(tile, Color.Green);
						}
						else {
							colorTile(tile, Color.Red);
						}
					}
				}
			}
		}
		else if (mode == Mode.debug) {
			// do something, like display infos of MousePosition tile(s)
		}
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
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
			System.out.println("tiles ratio = "+ UNITS_BUILD_RATIO);

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

					// buildings tile first
					tileBuild = new Tile(Tile.SIZE_BUILD);

					tileBuild.setGrid(gridBuild);
					tileBuild.setTilePosition(row, column);

					tileBuild.setSpecs(Specs.BUILDABLE, buildable);

					this.gridBuild[row][column] = tileBuild;
				}
			}

			// apply building specs to unit tiles (walk tiles)
			for (int y = 0; y < this.gridUnits.length; y++) {
				for (int x = 0; x < this.gridUnits[0].length; x++) {
					tileUnits = new Tile(Tile.SIZE_UNIT);

					walkable = game.isWalkable(x, y);

					tileUnits.setGrid(gridUnits);
					tileUnits.setTilePosition(y, x);

					tileUnits.setSpecs(Specs.WALKABLE, walkable);

					this.gridUnits[y][x] = tileUnits;
				}
			}

			System.err.println("Tiles : done !");
		}
	}

	public void saveTiles() {
		boolean debug = false;
		if (debug = true) return; // don't save until tiles code debugged

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
		return getBuildTile(tp);
	}

	public Tile getBuildTile(TilePosition tp) {
		Tile tile = gridBuild[tp.getY()][tp.getX()];

		// highlight this tile?
		colorTile(tile, Color.Purple);

		return tile;
	}

	public Tile[] getBuildTiles(Unit unit) {
		List<Tile> tiles = new ArrayList<Tile>();
		TilePosition up = unit.getTilePosition(), tp;

		for (int row = 0; row < unit.getType().tileHeight(); row++) {
			for (int col = 0; col < unit.getType().tileWidth(); col++) {
				tp = new TilePosition(up.getX() + col, up.getY() + row);
				tiles.add(getBuildTile(tp));
			}
		}

		return tiles.toArray(new Tile[0]);
	}

	public Tile getUnitTile(Position p) {
		TilePosition tp = new TilePosition(p.getX() / Tile.SIZE_UNIT, p.getY() / Tile.SIZE_UNIT);
		Tile tile = gridUnits[tp.getY()][tp.getX()];

		// highlight this tile?
		colorTile(tile, Color.Purple);

		return tile;
	}

	public int getBuildRowsCount() {
		return gridBuild.length;
	}

	public int getBuildColsCount() {
		return gridBuild[0].length;
	}

	public int getUnitsRowsCount() {
		return gridUnits.length;
	}

	public int getUnitsColsCount() {
		return gridUnits[0].length;
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
	public void onUnitDiscover(Unit unit) {
		//
	}

	@Override
	public void onUnitEvade(Unit unit) {
		//
	}

	@Override
	public void onUnitShow(Unit unit) {
		//
	}

	@Override
	public void onUnitHide(Unit unit) {
		//
	}

	@Override
	public void onUnitCreate(Unit unit) {
		//
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		//

	}

	@Override
	public void onUnitMorph(Unit unit) {
		//
	}

	@Override
	public void onUnitRenegade(Unit unit) {
		//
	}

	@Override
	public void onUnitComplete(Unit unit) {
		if (unit.getType().isBuilding()) {
			// make landing site of this unit unbuildable
			Tile[] tiles = getBuildTiles(unit);
			for (Tile tile : tiles) {
				tile.setSpecs(Specs.BUILDABLE, false);
			}
		}
	}

	protected Filter filter = new Filter() {
		@Override
		public boolean allow(Player player) {
			return true; // watch all players
		}

		@Override
		public boolean allow(Unit unit) {
			// needs manager listens to all units events
			return true;
		}
	};

	@Override
	public Filter getFilter() {
		return this.filter;
	}

	@Override
	public void onSendText(String text) {

	}

	@Override
	public void onReceiveText(Player player, String text) {
		AI.getGame().printf("Received : "+ text);
		if (text.startsWith("tiles")) {
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
}
