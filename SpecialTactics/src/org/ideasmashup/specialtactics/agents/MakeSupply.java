package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.listeners.UnitListener;
import org.ideasmashup.specialtactics.managers.Agents;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.managers.Units.Filter;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedResources;
import org.ideasmashup.specialtactics.needs.NeedUnit;

import bwapi.Position;
import bwapi.Race;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.Chokepoint;




public class MakeSupply extends DefaultAgent implements Consumer, UnitListener {

	protected UnitType supplyType;
	protected Unit supply;
	protected Position pos;
	protected Unit worker;

	protected enum State {
		START,
		READY,
		MOVING,
		BUILDING,
		DONE
	};
	protected State state;
	private State prevState;

	public MakeSupply() {
		super();
		this.pos = null;
		this.worker = null;
		this.supply = null;

		initNeeds();
	}

	protected void initNeeds() {
		// FIXME use polymorphism to implement race-specific
		//       supply creation agents (?)

		state = State.START;
		prevState = null;

		// first need a worker to build the depot it will be "booked temporarily"
		// so that the "next in line" consumer will already have it
		System.out.println("SUPPLY : Requested next worker for supply building");
		Needs.getInstance().add(new NeedUnit(this, Units.Types.WORKERS.getUnitType(), 0));

		// then we must "reserve" 100 minerals to be able to build the supply
		// FIXME ugly workaround to prevent minerals and gas for the pylons/dpot
		//       to be eaten up by other minerals consumers
		supplyType = Units.Types.SUPPLY.getUnitType();

		Resources.getInstance().reserveMinerals(supplyType.mineralPrice(), this);
		Resources.getInstance().reserveGas(supplyType.gasPrice(), this);

		System.out.println("SUPPLY : Requested ressources for supply building");
		Needs.getInstance().add(new NeedResources(this,
			Units.Types.SUPPLY.getUnitType().mineralPrice(),
			Units.Types.SUPPLY.getUnitType().gasPrice()
		));

		// auto-register agent
		Agents.getInstance().add(this);

		// auto-watch building construction
		Units.getInstance().addListener(this);
	}

	@Override
	public void update() {
		super.update();

		if (prevState != state) {
			System.out.println("SUPPLY : state = "+ state);
			prevState = state;
		}

		switch (state) {
			case START:
				break;
			case READY:
			case MOVING:
				TilePosition tp = worker.getTilePosition();

				if (AI.getGame().canBuildHere(worker, tp, supplyType)) {
					// when moving try to find a place where a supply structure can be built
					AI.say("found buildable site for new supply");

					if (worker.build(tp, supplyType)) {
						// ok building started!!
						AI.say("building placed for building...");
						Resources.getInstance().unreserve(this);
						state = State.BUILDING;
					}
					else {
						AI.say("cannot build supply here...");
					}
				}

				break;
			case BUILDING:
				if (AI.getPlayer().getRace() == Race.Protoss) {
					// building is done, release worker
					state = State.DONE;
				}
				break;
			case DONE:
				// remove from managers
				Agents.getInstance().remove(this);
				Units.getInstance().removeListener(this);

				// kill this agent and free its worker
				this.destroy();

				// nullify refs
				this.supply = null;
				this.pos = null;

				// release captured worker
				freeWorker();
				break;
		}
	}

	@Override
	public Need[] getNeeds(boolean returnAll) {
		return new Need[0];
	}

	@Override
	public boolean fillNeeds(Object offer) {
		if (offer instanceof Unit) {
			Unit unit = (Unit) offer;
			if (unit.getType().isWorker()) {
				System.out.println("SUPPLY : Received new worker #"+ unit.getID() +"!");
				state = State.READY;

				// assign worker
				this.worker = unit;

				// force unit to move to choke
				Chokepoint cp = BWTA.getNearestChokepoint(unit.getPosition());
				this.pos = new Position(cp.getCenter().getX(), cp.getCenter().getY());

				System.out.println("SUPPLY : Moving worker #"+ unit.getID() +" to choke point!");
				worker.move(pos);
				state = State.MOVING;

				return true;
			}
		}
		else {
			// non-unit offer : assume minerals
		}

		return false;
	}

	@Override
	public void onUnitDiscover(Unit unit) {}

	@Override
	public void onUnitEvade(Unit unit) {}

	@Override
	public void onUnitShow(Unit unit) {
		if (unit == worker) {
			// zerg worker unburrowed...
		}
	}

	@Override
	public void onUnitHide(Unit unit) {
		if (unit == worker) {
			// zerg worker burrowed...
		}
	}

	@Override
	public void onUnitCreate(Unit unit) {
		//
		if (unit.getBuildUnit() == worker) {
			//
		}
	}

	@Override
	public void onUnitDestroy(Unit unit) {
		if (unit == worker) {
			// worker killed :O
		}
		else if (unit == supply) {
			// supply killed :<
		}
	}

	@Override
	public void onUnitMorph(Unit unit) {
		if (unit == worker) {
			// zergs only
		}
		else if (unit == supply) {
			// zergs only
		}
	}

	@Override
	public void onUnitRenegade(Unit unit) {
		if (unit == worker) {
			// worker mind controlled... ><°
		}
		else if (unit == supply) {
			// zergs only (depots and plons not mind-controllable)
		}
	}

	@Override
	public void onUnitComplete(Unit unit) {
		if (unit.getType() == supplyType && unit.getPlayer() == AI.getPlayer()) {
			// assume the supply unit just built is from our worker... (this is very dodgy!!!)
			this.state = State.DONE;
		}
	}


	protected void freeWorker() {
		// liberate worker for other consumers to reclaim it
		// unless we are zerg and the unit has morphed and thus vanished
		if (worker.getPlayer().getRace() != Race.Zerg) {
			Units.getInstance().onUnitComplete(worker);
		}
		this.worker = null;
	}

	protected Filter filter = new Filter() {
		@Override
		public boolean allow(Unit unit) {
			return unit == worker;
		};
	};

	@Override
	public Filter getFilter() {
		return this.filter;
	}
}
