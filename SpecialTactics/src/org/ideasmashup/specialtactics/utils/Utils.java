package org.ideasmashup.specialtactics.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ideasmashup.specialtactics.agents.Agent;
import org.ideasmashup.specialtactics.needs.Need;

import bwapi.Game;
import bwapi.Player;
import bwapi.UnitType;

public class Utils {

	private static Utils instance = null;

	private final Player player;
	private final Game game;

	// TODO split all in distintc maps per categories (units, ressources, etc)
	private final Map<Need, Agent> needs;

	// map of all unit-specific listeners
	private final Map<UnitType, ArrayList<UnitListener>> lsUnits;

	protected Utils(Game game) {
		this.game = game;
		this.player = game.self();

		this.needs = new HashMap<Need, Agent>();
		this.lsUnits = new HashMap<UnitType, ArrayList<UnitListener>>();
	}

	public static Utils init(Game game) {
		if (instance == null) {
			instance = new Utils(game);
		}

		return instance;
	}

	public static Utils get() {
		return instance;
	}

	public Player getPlayer() {
		return player;
	}

	public Game getGame() {
		return game;
	}

	public UnitType getTypeFor(UType type){
		return type.get(getPlayer().getRace());
	}

	public void addNeed(Need need, Agent owner) {
		this.needs.put(need, owner);
	}

	public void removeNeed(Need need) {
		this.needs.remove(need);
	}

	public void removeNeeds(Agent owner) {
		// remove all occurences with "owner" value
		// oher syntaxes would only removes the first occurence
		this.needs.values().removeAll(Collections.singleton(owner));
	}

	public void addUnitsListener(UnitType type, UnitListener ls) {
		if (this.lsUnits.containsKey(type)) {
			this.lsUnits.get(type).add(ls);
		}
		else {
			// need to create a new ArrayList
			ArrayList<UnitListener> list = new ArrayList<UnitListener>();
			list.add(ls);

			this.lsUnits.put(type, list);
		}
	}

	public boolean removeUnitsListener(UnitType type, UnitListener ls) {
		if (this.lsUnits.containsKey(type)) {
			return this.lsUnits.get(type).remove(ls);
		}

		return false;
	}

}
