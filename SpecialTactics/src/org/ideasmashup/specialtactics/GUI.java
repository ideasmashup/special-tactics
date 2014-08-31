package org.ideasmashup.specialtactics;

import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ideasmashup.specialtactics.brains.Brain;

import processing.core.PApplet;

public class GUI extends JFrame {

	private static final long serialVersionUID = -7875436614181661200L;

	private static final String TASKLIST = "tasklist";
	private static final String KILL = "taskkill /IM ";
	private static final String GAME_PROCESS = "StarCraft.exe";

	private final AI ai;
	private final Brain brain;
	private final UpdatesThread updater;
	private final boolean updating;

	private PApplet sketch;
	private JPanel panel;

	public GUI(AI ai, Brain brain) {
		super("Special Tactics - alpha");

		this.ai = ai;
		this.brain = brain;
		this.updater = new UpdatesThread();

		this.updating = true;

		initGUI();
		initAI();

		// run sketch
		sketch.init();

	}

	private void initAI() {
		// start thread that updates AI details to display items like queues...
		updater.start();
	}

	private void initGUI() {
		// create Processing sketch
		sketch = new RealtimeSketch();

		panel = new JPanel();
		panel.setBounds(20, 20, 600, 600);
		panel.add(sketch);
		add(panel);

		setAlwaysOnTop(true);
		pack();
		setLocation(new Point(10, 50));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
				//
			}

			@Override
			public void windowIconified(WindowEvent e) {
				//
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				//
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				//
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// must stop the AI from Starcraft
				// maybe close the game too ?
				try {
					if (isProcessRunning(GAME_PROCESS)) {
						killProcess(GAME_PROCESS);
					}
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
				//
			}

			@Override
			public void windowActivated(WindowEvent e) {
				//
			}
		});
	}

	private boolean isProcessRunning(String serviceName) throws Exception {
		Process p = Runtime.getRuntime().exec(TASKLIST);
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			//System.out.println(line);
			if (line.contains(serviceName)) {
				return true;
			}
		}

		return false;
	}

	private void killProcess(String serviceName) throws Exception {
		Runtime.getRuntime().exec(KILL + serviceName);
	}

	public class UpdatesThread extends Thread {
		public UpdatesThread() {
			//
		}

		@Override
		public void run() {
			while (updating) {
				// TODO update processing sketch data ?

				try {
					sleep(1000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void updateGraph() {

		}
	}

	// processing sketch

	public class RealtimeSketch extends PApplet {

		private static final long serialVersionUID = 3698978875827336499L;


		@Override
		public void setup() {
			size(500, 500);
			background(0);
		}

		@Override
		public void draw() {
			// from: http://www.sebastianoliva.com/en/en/2010/05/using-a-processing-sketch-as-a-java-component/trackback/

			background(0);
			fill(200);
			ellipseMode(CENTER);
			ellipse(mouseX, mouseY, 40, 40);

			}
		}
	}
}
