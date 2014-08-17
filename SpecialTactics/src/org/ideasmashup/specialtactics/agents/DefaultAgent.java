package org.ideasmashup.specialtactics.agents;

public class DefaultAgent implements Agent {

	protected boolean dead;

	public DefaultAgent() {
		this.dead = false;
	}

	/* (non-Javadoc)
	 * @see org.ideasmashup.specialtactics.agents.Agent#update()
	 */
	@Override
	public void update() {
		// called on every frame (or as frequently as possible)
		// unless dead
		if (this.dead) {
			return;
		}
	}

	/* (non-Javadoc)
	 * @see org.ideasmashup.specialtactics.agents.Agent#destroy()
	 */
	@Override
	public void destroy() {
		// kill this agent
		this.dead = true;
	}

	/* (non-Javadoc)
	 * @see org.ideasmashup.specialtactics.agents.Agent#isDestroyed()
	 */
	@Override
	public boolean isDestroyed() {
		return dead;
	}

}
