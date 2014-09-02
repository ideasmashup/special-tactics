package org.ideasmashup.specialtactics.agents;

import org.ideasmashup.specialtactics.AI;
import org.ideasmashup.specialtactics.FrameRate;

import bwapi.Game;
import bwapi.Position;

/**
 * Clock agent
 * Measures and prints elapsed time.
 * This will be useful for build order execution and scouting analysis.
 *
 * @author Kevin POULET <github at ideasmashup.com>
 *
 */
public class Clock extends DefaultAgent {

	protected Game game;

	protected double gameTime;
	protected double realTime;

	protected Position textPos = new Position(10, 10);
	protected Position clockPos = new Position(60, 10);

	public Clock() {
		super();
		init();
	}

	protected void init() {
		game = AI.getGame();
		realTime = 0D;
		gameTime = 0D;
	}

	@Override
	public void update() {
		double fps = game.getFPS();
		if(fps <= 0)
			fps = FrameRate.NORMAL.getFPS();
		realTime += 1D / fps;
		gameTime += 1D / FrameRate.NORMAL.getFPS();
		game.drawTextScreen(textPos.getX(), textPos.getY(),
			"CLOCK" + "\n" +
			"Real:" + "\n" +
			"Game:"
		);
		game.drawTextScreen(clockPos.getX(), clockPos.getY(),
			"\n" +
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
