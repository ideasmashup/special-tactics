package org.ideasmashup.specialtactics.tiles;

import java.util.Date;

public class Trail {

	protected float score;
	protected Date lastChange;

	public Trail() {
		this.score = 0;
		this.lastChange = new Date();
	}

	public void setScore(float value) {
		this.score = value;
		this.lastChange = new Date();
	}

	public float getScore() {
		return this.score;
	}

	public void decrease() {
		// dilute score of this trail by 1%
		//
		this.score *= 0.01;
		this.lastChange = new Date();
	}

	public void decrease(float diluting_factor) {
		this.score *= diluting_factor;
		this.lastChange = new Date();
	}

	public void increase() {
		this.score += 1f;
		this.lastChange = new Date();
	}

	public void increase(float value) {
		this.score += value;
		this.lastChange = new Date();
	}

	public long getAge() {
		return new Date().getTime() - lastChange.getTime();
	}
}
