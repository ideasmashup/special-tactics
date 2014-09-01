package org.ideasmashup.specialtactics.managers;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ideasmashup.specialtactics.agents.Producer;
import org.ideasmashup.specialtactics.needs.Need;
import org.ideasmashup.specialtactics.needs.NeedUnit;

public class Producers {

	protected LinkedList<Producer> producers;
	protected Map<Producer, LinkedList<Need>> consumers;

	protected static Producers instance = null;

	protected Producers() {
		this.producers = new LinkedList<Producer>();
		this.consumers = new HashMap<Producer, LinkedList<Need>>();
	}

	public static Producers getInstance() {
		if (instance == null) {
			instance = new Producers();

			System.out.println("Producers initialized");
		}

		return instance;
	}

	public void addProducer(Producer producer) {
		producers.add(producer);
	}

	public void removeProducer(Producer producer) {
		producers.remove(producer);

		// FIXME find a way to move consumers of destroyed producer
		//       to another producer or a waiting queue?
		System.err.println("NOT IMPLEMENTED!!! producer removed - if it has consumers they'll all be lost forever!!");
		consumers.remove(producer);
	}

	public boolean addConsumer(Need need) {
		boolean added = false;

		if (need instanceof NeedUnit) {
			// check first (available ?) producer that can produce needed unit
			for (Producer p : producers) {
				if (p.canFill(need)) {
					p.addConsumer(need.getOwner(), need);
					if (consumers.containsKey(p)) {
						consumers.get(p).addLast(need);
					}
					else {
						consumers.put(p, new LinkedList<Need>());
						consumers.get(p).addLast(need);
					}

					added = true;
					break;
				}
			}
		}

		return added;
	}

	public boolean canProduce(Need need) {
		if (need instanceof NeedUnit) {
			// check first (available ?) producer that can produce needed unit
			for (Producer p : producers) {
				if (p.canFill(need)) {
					return true;
				}
			}
		}

		return false;
	}

	public List<Producer> getProducers() {
		return Collections.unmodifiableList(producers);
	}

	public int getProducersCount() {
		return producers.size();
	}

	public List<Producer> getProducers(Need need) {
		List<Producer> list = new LinkedList<Producer>();

		if (need instanceof NeedUnit) {
			// check where unit can be produced in all available producers
			for (Producer p : producers) {
				if (p.canFill(need)) {
					list.add(p);
				}
			}
		}

		return list;
	}

	public List<Need> getConsumers(Producer producer) {
		if (consumers.containsKey(producer)) {
			return Collections.unmodifiableList(consumers.get(producer));
		}
		else {
			return Collections.unmodifiableList(new LinkedList<Need>());
		}
	}

	public List<Need> getConsumers() {
		List<Need> list = new LinkedList<Need>();

		for (LinkedList<Need> cons : consumers.values()) {
			list.addAll(cons);
		}

		return Collections.unmodifiableList(list);
	}

	public int getConsumersCount() {
		int count = 0;

		for (LinkedList<Need> cons : consumers.values()) {
			count += cons.size();
		}

		return count;
	}

}
