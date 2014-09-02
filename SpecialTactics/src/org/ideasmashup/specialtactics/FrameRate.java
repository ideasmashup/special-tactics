package org.ideasmashup.specialtactics;

/**
 * Frame rates
 * Standard frame rates used in StarCraft
 *
 * @see https://code.google.com/p/bwapi/wiki/StarcraftGuide?wl=en#What_is_Starcraft's_frame_rate?
 * @author Kevin POULET <github at ideasmashup.com>
 *
 */
public enum FrameRate {

	SLOWEST(167),
	SLOWER(111),
	SLOW(83),
	NORMAL(67),
	FAST(56),
	FASTER(48),
	FASTEST(42),
	FASTEST_x2(21),
	FASTEST_x4(10),
	FASTEST_x8(5);

	private int localSpeed;

	private FrameRate(final int localSpeed) {
		this.localSpeed = localSpeed;
	}

	public int getLocalSpeed() {
		return localSpeed;
	}

	public double getFPS() {
		return 1000 / localSpeed;
	}

}
