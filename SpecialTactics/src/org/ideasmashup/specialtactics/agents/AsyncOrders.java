package org.ideasmashup.specialtactics.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.ideasmashup.specialtactics.managers.Agents;

/**
 * This agent aims to stack orders coming from external threads (such as Swing thread) that should be executed by the game.
 *
 * @author Kevin POULET <github at ideasmashup.com>
 *
 */
public class AsyncOrders extends DefaultAgent {

	private static AsyncOrders instance;

	private List<Runnable> orders;

	private AsyncOrders() {
		super();
		orders = Collections.synchronizedList(new ArrayList<Runnable>());
		Agents.getInstance().add(this);
	}

	public static AsyncOrders getInstance() {
		if(instance == null)
			instance = new AsyncOrders();
		return instance;
	}

	public void addOrder(final Runnable order) {
		synchronized(orders) {
			orders.add(order);
		}
	}

	@Override
	public void update() {
		synchronized(orders) {
			final Iterator<Runnable> iterator = orders.iterator();
			while(iterator.hasNext())
				iterator.next().run();
			orders.clear();
		}
	}

}
