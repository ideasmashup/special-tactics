package org.ideasmashup.specialtactics.agents;

import java.util.LinkedList;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.managers.Agents;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.managers.Units.Filter;
import org.ideasmashup.specialtactics.managers.Units.Types;

import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * Experimental demo code.
 *
 * This will be converted into a manager. For now this is a basic agent that
 * builds things from it's queue whenever there is resources to spend.
 *
 * @author William ANGER
 *
 */
public class ExperimentalProduction extends DefaultAgent implements UnitListener {

	protected LinkedList<UnitType> queue;
	protected LinkedList<MakeStructure> builders;

	public ExperimentalProduction() {
		queue = new LinkedList<UnitType>();
		builders = new LinkedList<MakeStructure>();

		// watch PROD_T1 events
		Units.getInstance().addListener(this);
	}

	@Override
	public void update() {
		super.update();

		// remove last builder if finished
		if (!builders.isEmpty()) {
			MakeStructure last = builders.getLast();
			if (last != null && last.isDestroyed()) {
				builders.remove(last);
				last = null;
			}
		}

		// start spending only when there is at least one supply structure
		if (AI.getPlayer().supplyTotal() >= 11 && Units.getInstance().getOwnBuildings(Types.PROD).size() < 6) {
			// check if we can spend the price for first queued item
			Resources res = Resources.getInstance();
			UnitType first = Types.PROD_T1.getUnitType();//queue.getFirst();

			if (first != null
				&& res.getUnusedMinerals() >= first.mineralPrice()
				&& res.getUnusedGas() >= first.gasPrice()
				&& builders.size() == 1) {

				// build structure asap using a "builder agent" (structure is built asap)
				MakeStructure builder = new MakeStructure(first, true);
				builders.addFirst(builder);
				//queue.removeFirst();

				System.out.println("Production : created new builder for "+ first);
				System.out.println("Production : waiting requests = "+ queue.size());
			}
		}
	}

	@Override
	public void onUnitDiscover(Unit unit) {
		// TODO Auto-generated method stub

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
	public void onUnitDestroy(Unit unit) {
		// TODO Auto-generated method stub

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
		// this is a T1 production structure... attach UnitProductionAgent
		System.out.println("Production : completed "+ unit +" #"+ unit.getID() +" creating producer...");
		ExperimentalProducer ep = new ExperimentalProducer(unit);
		Agents.getInstance().add(ep);
	}

	protected Filter filter = new Filter() {
		@Override
		public boolean allow(Player player) {
			return player == AI.getPlayer();
		}

		@Override
		public boolean allow(Unit unit) {
			return Types.PROD_T1.is(unit);
		}
	};

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return filter;
	}
}
