package org.ideasmashup.specialtactics;

import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gicentre.utils.multisketch.EmbeddedSketch;
import org.gicentre.utils.stat.BarChart;
import org.ideasmashup.specialtactics.brains.Brain;
import org.ideasmashup.specialtactics.brains.BrainListener;

import processing.core.PApplet;
import processing.core.PFont;

public class GUI implements BrainListener {

	private static final int WIDTH = 600;
	private static final int HEIGHT = 800;

	private final Brain brain;
	private final UpdatesThread updater;
	private final boolean updating;

	private PApplet sketch;
	private JPanel panel;
	private final JFrame frame;

	public GUI(Brain brain) {

		this.frame = new JFrame("AI Viewer");

		this.brain = brain;
		this.updater = new UpdatesThread();

		this.updating = true;

		initGUI();
		initAI();

		// attach itself to brain events
		brain.setListener(this);

		// run sketch
		sketch.init();
	}

	private void initAI() {
		// start thread that updates AI details to display items like queues...
		updater.start();
	}

	private void initGUI() {
		// create Processing "sketch" (see docs)
		sketch = new ProcessingApplet();

		buildLayout();
		plugBehavior();
	}

	private void buildLayout() {
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setSize(WIDTH, HEIGHT);
		panel.add(sketch);

		frame.add(panel);
	}

	private void plugBehavior() {
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

	public void show(boolean visible) {
		frame.setVisible(visible);
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

	public class BlankSketch extends EmbeddedSketch {

		private static final long serialVersionUID = -5577823635847454133L;

		@Override
		public void setup() {
			// do nothing...
		}
	}

	public class ProcessingApplet extends PApplet {

		private static final long serialVersionUID = 3698978875827336499L;

		// Sketch to show how to construct a slide show containg text, images
		// and sketches.
		// Can use the full screen but only if Hansi Raber's 'fullscreen'
		// library is installed.
		// Version 1.4, 10th August, 2010.
		// Author Jo Wood, giCentre.

		// ------------------ Sketch-wide variables ---------------------------

		BarChart barChart;
		PFont titleFont, smallFont;

		// ------------------ Initialisation ----------------------------------

		@Override
		public void setup() {
			// don't remove try catch otherwise errors not shown!!
			try {
				size(GUI.WIDTH, GUI.HEIGHT);

				// /////////////// Setup sketch ///////////////////////////
				smooth();
				noLoop();

				titleFont = loadFont("Helvetica-22.vlw");
				smallFont = loadFont("Helvetica-12.vlw");
				textFont(smallFont);

				barChart = new BarChart(this);
				barChart.setData(new float[] { 2462, 2801, 3280, 3983, 4490, 4894, 5642, 6322,
						6489, 6401, 7657, 9649, 9767, 12167, 15154, 18200, 23124, 28645 });
				barChart.setBarLabels(new String[] { "1830", "1840", "1850", "1860", "1870",
						"1880", "1890", "1900", "1910", "1920", "1930", "1940", "1950", "1960",
						"1970", "1980", "1990", "2000" });
				barChart.setBarColour(color(200, 80, 80, 100));
				barChart.setBarGap(2);
				barChart.setValueFormat("$###,###");
				barChart.showValueAxis(true);
				barChart.showCategoryAxis(true);
				// ///////////////////////////////////////////////////////
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ----------------- Processing draw ----------------------------------

		@Override
		public void draw() {
			// from: http://www.sebastianoliva.com/en/en/2010/05/using-a-processing-sketch-as-a-java-component/trackback/

			// from: http://blog.blprnt.com/blog/blprnt/tutorial-processing-javascript-and-data-visualization

			// don't remove try catch otherwise errors not shown!!
			try {
				// /////////////// Draw sketch ///////////////////////////
				background(255);
				barChart.draw(10, 10, width - 50, height - 50);
				fill(120);
				textFont(titleFont);
				text("Units in production", 70, 30);
				float textHeight = textAscent();
				textFont(smallFont);
				text("Total production per UnitType", 70,
						30 + textHeight);
				// ///////////////////////////////////////////////////////
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ------------------ Processing keyboard handling --------------------

		@Override
		public void keyPressed() {
			if (key == ' ') {
				// exit on spacebar
				AI.terminate(0);
			}

			if (key == CODED) {
				// Up and down arrows control the speed of animation.
				if (keyCode == UP) {
					//
				}
				else if (keyCode == DOWN) {
					//
				}
				else if (keyCode == RIGHT) {
					//
				}
				else if (keyCode == LEFT) {
					//
				}
			}
		}

		// -------------------------- Nested classes --------------------------

		// -------------------------- Private methods -------------------------

	}
}
