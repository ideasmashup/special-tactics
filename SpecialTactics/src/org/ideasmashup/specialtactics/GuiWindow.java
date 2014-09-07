package org.ideasmashup.specialtactics;

import javax.swing.JFrame;

public class GuiWindow {

	protected final JFrame frame;

	public GuiWindow(String title) {
		super();
		this.frame = new JFrame(title);

		buildLayout();
		plugBehavior();
	}

	protected void buildLayout() {
		//
	}

	protected void plugBehavior() {
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public void show(boolean visible) {
		frame.setVisible(visible);
	}

}
