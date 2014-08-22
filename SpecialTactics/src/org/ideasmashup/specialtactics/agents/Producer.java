package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.needs.Need;

public interface Producer {
	public boolean canFill(Need need);
	public void addConsumer(Consumer consumer, Need need);
}
