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

import bwapi.Player;
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
		BUILD_TRY,
		BUILDING,
		DONE
	};

	protected State state;
	private State prevState;

	protected NeedResources needResources;
	protected NeedUnit needUnit;

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
		needUnit = new NeedUnit(this, Units.Types.WORKERS.getUnitType(), 0);
		Needs.getInstance().addNeed(needUnit);

		// then we must "reserve" 100 minerals to be able to build the supply
		// FIXME ugly workaround to prevent minerals and gas for the pylons/dpot
		//       to be eaten up by other minerals consumers
		supplyType = Units.Types.SUPPLY.getUnitType();

		System.out.println("SUPPLY : Requested ressources for supply building");
		needResources = new NeedResources(this,
			Units.Types.SUPPLY.getUnitType().mineralPrice(),
			Units.Types.SUPPLY.getUnitType().gasPrice()
		);
		Needs.getInstance().addNeed(needResources);

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
				// already has a worker
				Needs.getInstance().removeNeed(needUnit);
			case BUILD_TRY:
				// new state because build can fail even when build() returns true
				// so building must only start after we have validated that a
				// supply unit has been actually created
			case MOVING:
				// move and wait for adequate resources in fillNeed()
				// try to build now
				TilePosition tp = worker.getTilePosition();

				if (AI.getGame().canBuildHere(worker, tp, supplyType)) {
					// when moving try to find a place where a supply structure can be built
					System.out.println("SUPPLY found buildable site for new supply");

					if (worker.build(tp, supplyType)) {
						// ok building started!!
						System.out.println("SUPPLY building building attempt seems ok...");
						state = State.BUILD_TRY;
					}
					else {
						System.err.println("SUPPLY cannot build supply here...");
					}
				}

				break;
			case BUILDING:
				// release reserved locks
				Resources.getInstance().unreserve(this);
				Needs.getInstance().removeNeed(needResources);

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
		return new Need[]{needResources};
	}

	@Override
	public boolean canReceiveOffer() {
		return state != State.BUILDING && state != State.DONE && !isDestroyed();
	}

	@Override
	public boolean fillNeeds(Object offer) {
		if (state == State.DONE) return false;

		if (offer instanceof Unit) {
			Unit unit = (Unit) offer;
			if (unit.getType().isWorker() && worker == null) {
				System.out.println("SUPPLY : Received new worker #"+ unit.getID() +"!");
				state = State.READY;

				// assign worker
				this.worker = unit;

				// force unit to move to choke
				Chokepoint cp = BWTA.getNearestChokepoint(unit.getPosition());
				this.pos = new Position(cp.getCenter().getX(), cp.getCenter().getY());

				System.out.println("SUPPLY : Moving worker #"+ unit.getID() +" to choke point!");
				worker.patrol(pos);
				state = State.MOVING;

				return true;
			}
			else {
				return false;
			}
		}
		else {
			// non-unit offer : assume minerals
			Resources res = Resources.getInstance();
			if ((state == State.MOVING || state == State.READY)
				&& res.getMinerals(this) >= needResources.getMinerals()
				&& res.getGas(this) >= needResources.getGas()) {

				// must lock resources until worker reaches build location)
				if (!res.hasReserved(this)) {
					res.reserveMinerals(needResources.getMinerals(), this);
					res.reserveGas(needResources.getGas(), this);
				}

				return true;
			}
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
		if (unit.getType() == supplyType && state == State.BUILD_TRY) {
			// we are trying to build so this new supply unit should be ours...
			state = State.BUILDING;
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
		if (unit.getType() == supplyType && state == State.BUILDING) {
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
		public boolean allow(Player player) {
			return player == AI.getPlayer();
		}

		@Override
		public boolean allow(Unit unit) {
			// check for agent's worker and supply unit creation
			return unit == worker || unit.getType() == supplyType;
		}
	};

	@Override
	public Filter getFilter() {
		return this.filter;
	}
}
