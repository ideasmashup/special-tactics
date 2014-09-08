package org.ideasmashup.specialtactics;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ideasmashup.specialtactics.brains.Brain;

public class GUI extends GuiWindow {

	public static final int WIDTH = 600;
	public static final int HEIGHT = 200;

	private final Brain brain;

	private JPanel panel;

	private GuiWindow wndSpeed;
	private GuiWindow wndStats;

	public GUI(Brain brain) {
		super("AI Dashboard");
		this.wndSpeed = null;
		this.wndStats = null;

		this.brain = brain;

		// FIXME load GUI elements/frames to open from config file
	}

	@Override
	protected void buildLayout() {
		super.buildLayout();

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setSize(WIDTH, HEIGHT / 5);

		// setup panel... with buttons
		JButton button;

		button = new JButton("Game speed");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (wndSpeed == null) {
					wndSpeed = new GuiSpeed();
					wndSpeed.show(true);
				}
			}
		});
		panel.add(button);

		button = new JButton("Game stats");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (wndStats == null) {
					wndStats = new GuiStats(brain);
					wndStats.show(true);
				}
			}
		});
		panel.add(button);

		frame.add(panel);
	}

	@Override
	protected void plugBehavior() {
		frame.setAlwaysOnTop(true);
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		frame.setLocation(new Point(10, 50));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// must stop the AI from Starcraft
				// maybe close the game too ?
				AI.terminate(0);
			}
		});
	}
}
