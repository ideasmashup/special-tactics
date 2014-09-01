package org.ideasmashup.specialtactics.agents.scouts;

import java.util.List;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.agents.UnitAgent;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.managers.Units.Types;

import bwapi.Color;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Polygon;
import bwta.Region;

/**
 * Scouting sub-agent
 * Its job is to turn inside a region in order to gather intelligence.
 *
 * @author Kevin POULET <github at ideasmashup.com>
 *
 */
public class CircleBase extends UnitAgent {

	private Region region;
	private Polygon polygon;
	private Position nextPosition;

	public CircleBase(final Unit bindee) {
		super(bindee);
		final Position position = bindee.getPosition();
		final BaseLocation b = BWTA.getNearestBaseLocation(position);
		region = b.getRegion();
		polygon = region.getPolygon();
		System.err.println("polygon => center=" + polygon.getCenter() + " area=" + polygon.getArea() + " perimeter=" + polygon.getPerimeter());
		nextPosition = polygon.getNearestPoint(position);

		// start patrolling just to get some unit events (discovered/hidden/created...)
		final int ux = bindee.getPosition().getX();
		final int uy = bindee.getPosition().getY();
		final int dx = b.getPosition().getX() - ux;
		final int dy = b.getPosition().getY() - uy;
		boolean xGreater = Math.abs(dx) >= Math.abs(dy);
		bindee.patrol(new Position(ux - (xGreater ? dx : 0), uy - (xGreater ? 0 : dy)));

//		System.out.println("bindee position " + position.getX() + "/" + position.getY() + " valid ? " + position.isValid());
//		System.out.println("nextposition " + nextPosition.getX() + "/" + nextPosition.getY() + " valid ? " + nextPosition.isValid());
////		if(!nextPosition.isValid()) {
////			nextPosition = new Position(
////				nextPosition.getX() - (nextPosition.getX() < position.getX() ? -1 : 1) * bindee.getType().tileWidth(),
////				nextPosition.getY() - (nextPosition.getY() < position.getY() ? -1 : 1) * bindee.getType().tileHeight()
////			);
////			System.err.println("nextposition " + nextPosition.getX() + "/" + nextPosition.getY() + " valid ? " + nextPosition.isValid());
////		}
//		bindee.move(nextPosition);
	}

	@Override
	public void update() {
		final int sightRange = AI.getPlayer().sightRange(bindee.getType());
		Position bpos = bindee.getPosition();
		List<Unit> unitsInRange = AI.getGame().getUnitsInRadius(bpos, sightRange);
		for(final Unit unit : unitsInRange) {
			final Position up = unit.getPosition();
			final UnitType ut = unit.getType();
			AI.getGame().drawBoxMap(up.getX() - ut.dimensionLeft(), up.getY() - ut.dimensionUp(), up.getX() + ut.dimensionRight(), up.getY() + ut.dimensionDown(), Color.Red);
		}
//		final double distanceToTarget = bindee.getPosition().getDistance(nextPosition);
//		if(distanceToTarget < 50) {
//			double angle = bindee.getAngle();
//			int ux = bpos.getX();
//			int uy = bpos.getY();
//			int dx = ux + (int) (sightRange * Math.cos(angle));
//			int dy = uy + (int) (sightRange * Math.sin(angle));
//			Position p = new Position(dx, dy);
//			if(!p.isValid()) {
//				p.makeValid();
//			}
//			nextPosition = p;
//			bindee.move(nextPosition);
////			polygon.isInside(p)
////			System.out.println("angle=" + Math.toDegrees(angle) + " cos=" + Math.cos(angle) + " sin=" + Math.sin(angle) + " x=" + ux + " y=" + uy + " dx=" + dx + " dy=" + dy);
////			AI.getGame().drawLineMap(ux, uy, dx, dy, Color.Green);
//		}
		AI.getGame().drawLineMap(bpos.getX(), bpos.getY(), nextPosition.getX(), nextPosition.getY(), Color.Green);
		AI.getGame().drawCircleMap(bpos.getX(), bpos.getY(), sightRange, Color.Blue);
	}

}
