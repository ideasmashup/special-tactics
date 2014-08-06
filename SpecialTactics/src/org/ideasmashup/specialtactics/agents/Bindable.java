package org.ideasmashup.specialtactics.agents;

import java.awt.Point;
import java.util.EventListener;

import bwapi.Unit;

public interface Bindable {
	public void bindToUnit(Unit unit);
	public void bindToTile(int x, int y);
	public void bindToPoint(int x, int y);

	public Unit getUnit();
	public Point getPoint();
	public Object getTile();

	public void update();
	
	public void addListener(EventListener ls);
	public void RemoveListener(EventListener ls);
	
}
