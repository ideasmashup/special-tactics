package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.AI;

import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;

/**
 * Clock agent
 * Measures and prints elapsed time.
 * This will be useful for build order execution and scouting analysis.
 *
 * @author Kevin POULET <github at ideasmashup.com>
 *
 */
public class Clock extends UnitAgent {

	protected Game game;

	protected double gameTime;
	protected double realTime;
	protected long t0;

	protected Position textPos = new Position(10, 10);
	protected Position clockPos = new Position(60, 10);

	// TODO move to more global class (Utils?)
	protected static final double FPS_NORMAL = 14.93;
	protected static final double FPS_FAST = 17.86;
	protected static final double FPS_FASTER = 20.83;

	public Clock(final Unit bindee) {
		super(bindee);
	}

	@Override
	protected void init() {
		game = AI.getGame();
		realTime = 0D;
		gameTime = 0D;
		t0 = System.currentTimeMillis();
	}

	@Override
	public void update() {
		double fps = game.getFPS();
		if(fps <= 0)
			fps = FPS_NORMAL;
		realTime += 1D / fps;
		gameTime += 1D / FPS_NORMAL;
		game.drawTextScreen(textPos.getX(), textPos.getY(),
			"CLOCK" + "\n" +
			"System:" + "\n" +
			"Real:" + "\n" +
			"Game:"
		);
		game.drawTextScreen(clockPos.getX(), clockPos.getY(),
			"\n" +
			timeToString((int) (System.currentTimeMillis() - t0) / 1000) + "\n" +
			timeToString((int) realTime) + "\n" +
			timeToString((int) gameTime)
		);
	}

	private static String timeToString(final long t) {
		final int mn = (int) (t / 60);
		final int sec = (int) (t % 60);
		return (mn < 10 ? "0" : "") + mn + ":" + (sec < 10 ? "0" : "") + sec;
	}

}
