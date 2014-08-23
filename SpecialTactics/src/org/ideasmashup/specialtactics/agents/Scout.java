package org.ideasmashup.specialtactics.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.managers.Units.Filter;
import org.ideasmashup.specialtactics.managers.Units.Types;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

/**
 * Scouting agent
 * Its main job is to accomplish scouting tasks such as finding enemy main base, looking for enemy expansions, checking presence of Templar archives or units upgrades.
 *
 * @author Kevin POULET <github at ideasmashup.com>
 *
 */
public class Scout extends UnitAgent implements UnitListener {

	private List<BaseLocation> enemyBases;
	private boolean scouting;
	private Queue<Position> wayPoints;

	private Map<Integer, Unit> myUnits;
	private Map<Integer, Unit> myBuildings;
	private Map<Integer, Unit> enemyUnits;
	private Map<Integer, Unit> enemyBuildings;

	public Scout(final Unit bindee) {
		super(bindee);
		this.init();
	}

	public void init() {
		enemyBases = new ArrayList<BaseLocation>();
		myUnits = new HashMap<Integer, Unit>();
		myBuildings = new HashMap<Integer, Unit>();
		enemyUnits = new HashMap<Integer, Unit>();
		enemyBuildings = new HashMap<Integer, Unit>();
	}

	@Override
	public void update() {
		// first get a unit for scouting
		// TODO use Needs system instead of kidnaping units
		if(bindee == null) {
			for (final Unit u : myUnits.values()) {
		        if (Units.Types.WORKERS.is(u)) {
		            bindee = u;
		            break;
		        }
		    }
		}
		if(bindee != null) {
			if(!scouting) {
				if(enemyBases.isEmpty()) {
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
				final int sightRange = AI.getPlayer().sightRange(bindee.getType());
				final double distanceToTarget = bindee.getPosition().getDistance(bindee.getTargetPosition());
				if(distanceToTarget < sightRange) {
					System.out.println("Scout unit in range of target position.");

					for (final Unit u : enemyBuildings.values()) {
					    if (Types.BASE.is(u)) {
					    	System.out.println("enemy base found");
					    	final BaseLocation b = BWTA.getNearestBaseLocation(u.getPosition());
					    	enemyBases.add(b);
					    	if(b.isStartLocation()) {
						    	System.out.println("enemy main found!");
						    	scouting = false;
						    	// start patrolling just to get some unit events (discovered/hidden/created...)
						    	final int ux = bindee.getPosition().getX();
						    	final int uy = bindee.getPosition().getY();
						    	final int dx = b.getPosition().getX() - ux;
						    	final int dy = b.getPosition().getY() - uy;
						    	boolean xGreater = Math.abs(dx) >= Math.abs(dy);
							    bindee.patrol(new Position(ux - (xGreater ? dx : 0), uy - (xGreater ? 0 : dy)));
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

	@Override
	public void onUnitDiscover(final Unit unit) {
		final UnitType type = unit.getType();
		if(unit.getPlayer().equals(AI.getPlayer())) {
			if(type.isBuilding()) {
				final Unit u = myBuildings.get(unit.getID());
				if(u == null) { // unknown unit
					System.out.println("Registering new own building");
					myBuildings.put(unit.getID(), unit);
				}
			} else {
				final Unit u = myUnits.get(unit.getID());
				if(u == null) { // unknown unit
					System.out.println("Registering new own unit");
					myUnits.put(unit.getID(), unit);
				}
			}
		} else if(unit.getPlayer().equals(AI.getGame().enemy())) {
			if(type.isBuilding()) {
				final Unit u = enemyBuildings.get(unit.getID());
				if(u == null) { // unknown unit
					System.out.println("Registering new enemy building");
					enemyBuildings.put(unit.getID(), unit);
				}
			} else {
				final Unit u = enemyUnits.get(unit.getID());
				if(u == null) { // unknown unit
					System.out.println("Registering new enemy unit");
					enemyUnits.put(unit.getID(), unit);
				}
			}
		}
	}

	@Override
	public void onUnitEvade(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitShow(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitHide(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitCreate(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitDestroy(final Unit unit) {
		final UnitType type = unit.getType();
		if(unit.getPlayer().equals(AI.getPlayer())) {
			if(type.isBuilding()) {
				final Unit u = myBuildings.get(unit.getID());
				if(u != null) { // known unit
					System.out.println("Unregistering own building");
					myBuildings.remove(unit.getID());
				}
			} else {
				final Unit u = myUnits.get(unit.getID());
				if(u != null) { // known unit
					System.out.println("Unregistering own unit");
					myUnits.remove(unit.getID());
					if(bindee == unit) {
						System.out.println("Scout unit destroyed!");
						bindee = null;
						scouting = false;
					}
				}
			}
		} else if(unit.getPlayer().equals(AI.getGame().enemy())) {
			if(type.isBuilding()) {
				final Unit u = enemyBuildings.get(unit.getID());
				if(u != null) { // known unit
					System.out.println("Unregistering enemy building");
					enemyBuildings.remove(unit.getID());
				}
			} else {
				final Unit u = enemyUnits.get(unit.getID());
				if(u != null) { // known unit
					System.out.println("Unregistering enemy unit");
					enemyUnits.remove(unit.getID());
				}
			}
		}
	}

	@Override
	public void onUnitMorph(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitRenegade(Unit unit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnitComplete(Unit unit) {
		// TODO Auto-generated method stub

	}

	protected Filter filter = new Filter() {
		@Override
		public boolean allow(Unit unit) {
			// scout listens to all units events so allow all units
			return true;
		};
	};

	@Override
	public Filter getFilter() {
		return this.filter;
	}
}
