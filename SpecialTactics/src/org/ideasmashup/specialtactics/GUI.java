package org.ideasmashup.specialtactics;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JFrame;

import org.ideasmashup.specialtactics.brains.Brain;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;

public class GUI extends JFrame {

	private static final long serialVersionUID = -7875436614181661200L;

	private final AI ai;
	private final Brain brain;
	private final UpdatesThread updater;

	private final boolean updating;

	private static final String TASKLIST = "tasklist";
	private static final String KILL = "taskkill /IM ";
	private static final String GAME_PROCESS = "StarCraft.exe";

	public GUI(AI ai, Brain brain) {
		super("Special Tactics - alpha");

		this.ai = ai;
		this.brain = brain;
		this.updater = new UpdatesThread();

		this.updating = true;

		initGUI();
	}

	private void initAI(AI ai, Brain brain) {
		// start thread that updates AI details to display items like queues...
		updater.start();
	}

	private void initGUI() {
		DirectedSparseGraph g = new DirectedSparseGraph();
		g.addVertex("Vertex1");
		g.addVertex("Vertex2");
		g.addVertex("Vertex3");
		g.addEdge("Edge1", "Vertex1", "Vertex2");
		g.addEdge("Edge2", "Vertex1", "Vertex3");
		g.addEdge("Edge3", "Vertex3", "Vertex1");
		VisualizationImageServer vs = new VisualizationImageServer(new CircleLayout(g),
				new Dimension(200, 200));

		getContentPane().add(vs);

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
			while (updating) {

			}
		}

		public void updateGraph() {

		}
	}
}
