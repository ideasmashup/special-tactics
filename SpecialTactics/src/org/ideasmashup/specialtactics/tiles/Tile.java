package org.ideasmashup.specialtactics.tiles;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.ideasmashup.specialtactics.managers.Tiles;

import bwapi.Position;
import bwapi.TilePosition;


public class Tile implements Serializable {

	// FIXME increase Tile resolution to "walkable" level (eg. 8*8px)
	//  instead of current "build" level (e.g. 32*32px)
	//  see : https://code.google.com/p/bwapi/wiki/Misc

	public static int WIDTH = 32;
	public static int HEIGHT = 32;

	private static final long serialVersionUID = -3384650743036528866L;

	protected Map<Tiles.Specs, Object> specs;
	protected Map<Tiles.Trails, Trail> trails;

	protected transient Tile[][] grid;
	protected transient int row, column;
	protected transient TilePosition tileposition;
	protected transient Position position;

	public Tile() {
		this.specs = new HashMap<Tiles.Specs, Object>();
		this.trails = new HashMap<Tiles.Trails, Trail>();

		this.row = -1;
		this.column = -1;
		this.grid = null;
	}

	public Object getSpecs(Tiles.Specs type) {
		return specs.get(type);
	}

	public void setSpecs(Tiles.Specs spec, Object value) {
		specs.put(spec, value);
	}

	public Trail getTrail(Tiles.Trails trail) {
		return trails.get(trail);
	}

	public Collection<Trail> getTrails() {
		return trails.values();
	}

	public void setGrid(Tile[][] grid) {
		this.grid = grid;
	}

	public void setTilePosition(int row, int column) {
		this.row = row;
		this.column = column;
		this.tileposition = new TilePosition(column, row);
		this.position = new Position(column * 32, row * 32);
	}

	public TilePosition getTilePosition() {
		return tileposition;
	}

	public Position getPosition() {
		return position;
	}
}
