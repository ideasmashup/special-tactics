package org.ideasmashup.specialtactics.agents;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.managers.Units.Types;
import org.ideasmashup.specialtactics.utils.Utils;

import bwapi.Position;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;

/**
 * Scouting agent
 * Its main job is to accomplish scouting tasks such as finding ennemy main base, looking for ennemy expansions, checking presence of Templar archives or units upgrades.
 * 
 * @author Kevin POULET <github at ideasmashup.com>
 *
 */
public class Scout extends Agent {

	private List<BaseLocation> ennemyBases;
	private boolean scouting;
	private Queue<Position> wayPoints;

	public Scout(final Unit bindee) {
		super(bindee);
	}

	@Override
	public void init() {
		ennemyBases = new ArrayList<BaseLocation>();
	}

	@Override
	public void update() {
		// first get a unit for scouting
		// TODO use Needs system instead of kidnapping units
		if(bindee == null) {
			for (final Unit u : Utils.get().getPlayer().getUnits()) {
		        if (Units.Types.WORKERS.is(u)) {
		            bindee = u;
		            break;
		        }
		    }
		}
		if(bindee != null) {
			if(!scouting) {
				if(ennemyBases.isEmpty()) {
					// lists all start locations
					final List<BaseLocation> sb = new ArrayList<BaseLocation>();
					final BaseLocation bSelf = BWTA.getStartLocation(Utils.get().getPlayer());
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
					final Position first = wayPoints.poll();
					if(first != null) {
						bindee.move(first);
						scouting = true;
					} else {
						System.err.println("No base to scout");
					}
				}
			} else {
				// checks building presence before actually reaching the location to avoid attacking the building
				final int sightRange = Utils.get().getPlayer().sightRange(bindee.getType());
				final double distanceToTarget = bindee.getPosition().getDistance(bindee.getTargetPosition());
				if(distanceToTarget < sightRange) {
					System.out.println("Scout unit in range of target position.");
					for (final Unit u : Utils.get().getGame().enemy().getUnits()) {
					    if (u.getType().isBuilding() && Types.BUILDING_BASE.is(u)) {
					    	System.out.println("Ennemy base found");
					    	final BaseLocation b = BWTA.getNearestBaseLocation(u.getPosition()); 
					    	ennemyBases.add(b);
					    	if(b.isStartLocation()) {
						    	System.out.println("Ennemy main found!");
						    	bindee.stop();
						    	scouting = false;
					    	}
					    }
					}
					if(scouting) { //== if main not found yet
						System.out.println("No main building found here, moving to next position.");
						final Position next = wayPoints.poll();
						if(next != null) {
							bindee.move(next);
						} else {
							System.err.println("still not found but no more base to scout");
						}
					}
				}
			}
		}
	}

}
