package org.ideasmashup.specialtactics;

import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.ideasmashup.specialtactics.brains.Brain;

public class GUI extends JFrame {

	private static final long serialVersionUID = -7875436614181661200L;

	private final AI ai;
	private final Brain brain;
	private final UpdatesThread updater;

	public GUI(AI ai, Brain brain) {
		super("Special Tactics - alpha");

		this.ai = ai;
		this.brain = brain;
		this.updater = new UpdatesThread();

		initGUI();
	}

	private void initAI(AI ai, Brain brain) {
		// start thread that updates AI details to display items like queues...
		updater.start();
	}

	private void initGUI() {
		setAlwaysOnTop(true);
		setSize(500, 800);
		setLocation(new Point(10, 50));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
		setVisible(true);
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
			super.run();
		}
	}
}
