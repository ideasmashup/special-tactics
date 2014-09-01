package org.ideasmashup.specialtactics.agents.scouts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.agents.UnitAgent;
import org.ideasmashup.specialtactics.managers.Agents;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.managers.Units.Types;

import bwapi.Position;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;

/**
 * Scouting sub-agent
 * Its job is to sort start locations by distance and send a unit to each of them until the enemy main base is found.
 *
 * @author Kevin POULET <github at ideasmashup.com>
 *
 */
public class FindEnemyMain extends UnitAgent {

	private Units units;

	private Queue<Position> wayPoints;
	private Position nextPosition;

	public FindEnemyMain(final Unit bindee) {
		super(bindee);
		this.init();
	}

	public void init() {
		units = Units.getInstance();
	}

	@Override
	public void update() {
		if(dead)
			return;
		// first get a unit for scouting
		// TODO use Needs system instead of kidnaping units
		if(bindee == null) {
			bindee = units.getOwnUnits(Types.WORKERS).iterator().next();
		}
		if(nextPosition == null) {
			// lists all start locations
			final List<BaseLocation> sb = new ArrayList<BaseLocation>();
			final BaseLocation bSelf = BWTA.getStartLocation(AI.getPlayer());
			for (BaseLocation b : BWTA.getBaseLocations()) {
				if (b.isStartLocation() && !b.getPosition().equals(bSelf.getPosition())) { // excludes own start location
					sb.add(b);
				}
			}
			// sorts by distance
			BaseLocation bFrom = bSelf;
			wayPoints = new LinkedList<Position>();
			while(!sb.isEmpty()) {
				BaseLocation bNearest = null;
				for(final BaseLocation b : sb) {
					if(bNearest == null || bFrom.getGroundDistance(b) < bFrom.getGroundDistance(bNearest))
						bNearest = b;
				}
				wayPoints.add(bNearest.getPosition());
				bFrom = bNearest;
				sb.remove(bNearest);
			}
			// starts scouting
			nextPosition = wayPoints.poll();
			if(nextPosition != null) {
				System.out.println("Moving to first position");
				bindee.move(nextPosition);
			} else {
				System.err.println("No base to scout");
			}
		} else {
			// checks building presence before actually reaching the location to save a few seconds and avoid attacking the building
			final int sightRange = AI.getPlayer().sightRange(bindee.getType());
			final double distanceToTarget = bindee.getPosition().getDistance(bindee.getTargetPosition());
			if(distanceToTarget < sightRange) {
				System.out.println("Scout unit in range of target position.");
				for (final Unit u : units.getEnemyBuildings(Types.BASE)) {
					System.out.println("enemy base found");
					final BaseLocation b = BWTA.getNearestBaseLocation(u.getPosition());
					if(b.isStartLocation()) {
						System.out.println("enemy main found!");
						dead = true;
						Agents.getInstance().add(new CircleBase(bindee));
					}
				}
				if(!dead) {
					if(!wayPoints.isEmpty()) {
						System.out.println("No main building found here, moving to next position.");
						nextPosition = wayPoints.poll();
						bindee.move(nextPosition);
					} else {
						System.err.println("still not found but no more base to scout");
						dead = true;
					}
				}
			}
		}
	}

}
