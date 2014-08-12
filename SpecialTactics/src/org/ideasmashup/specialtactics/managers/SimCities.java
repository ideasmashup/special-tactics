package org.ideasmashup.specialtactics.managers;

import org.ideasmashup.specialtactics.utils.Utils;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;

public class SimCities {
	protected static Supplies instance = null;

	protected SimCities() {
		// FIXME load preset building placements

	}

	public static void init() {
		if (instance == null) {
			instance = new Supplies();

			System.out.println("SimCities initialized");
		}
	}

	public static Position getLocationForStructure(Unit structure, Unit worker) {
		// extract the region containing the near
		bwta.Region region = BWTA.getRegion(worker.getPosition());
		bwta.Polygon polygon = region.getPolygon();
		Position center = region.getCenter();
		TilePosition tp;

		// FIXME implement a better way to find "correct" placements
		//       for now search random within the region containing the worker
		Position p;
		do {
			// this is awful because it can take a very long time
			p = new Position(
				center.getX() + (int) (Math.random()*40),
				center.getY() + (int) (Math.random()*40)
			);

			// ugly hack
			tp = new TilePosition(p.getX(), p.getY());

			// check that the position is buildable
			if (Utils.get().getGame().canBuildHere(worker, tp, structure.getType())) {

			}
		}
		while (!polygon.isInside(p));

		return p;
	}
}
