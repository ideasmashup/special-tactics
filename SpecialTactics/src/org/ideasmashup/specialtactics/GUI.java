package org.ideasmashup.specialtactics;

import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ideasmashup.specialtactics.brains.Brain;

import processing.core.PApplet;

public class GUI extends JFrame {

	private static final long serialVersionUID = -7875436614181661200L;

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
		// create Processing "sketch" (see docs)
		sketch = new ProcessingApplet();

		buildLayout();
		plugBehavior();
	}

	private void buildLayout() {
		GridBagConstraints gbc = new GridBagConstraints();

		panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("AI data visualization"));
		panel.add(sketch, gbc);

		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.weightx = gbc.weighty = 1;

		getContentPane().add(panel, gbc);
	}

	private void plugBehavior() {
		setAlwaysOnTop(true);
		setSize(500, 500);
		setResizable(false);
		setLocation(new Point(10, 50));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// must stop the AI from Starcraft
				// maybe close the game too ?
				AI.terminate(0);
			}
		});
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

	public class ProcessingApplet extends PApplet {

		private static final long serialVersionUID = 3698978875827336499L;

		@Override
		public void setup() {
			size(getParent().getWidth(), getParent().getHeight());
			//background(0);
		}

		@Override
		public void draw() {
			// from: http://www.sebastianoliva.com/en/en/2010/05/using-a-processing-sketch-as-a-java-component/trackback/

//			background(0);
//			fill(200);
			ellipseMode(CENTER);
			ellipse(mouseX, mouseY, 40, 40);

			// from: http://blog.blprnt.com/blog/blprnt/tutorial-processing-javascript-and-data-visualization

//			float[] numbers = new float[] {
//				13.4f, 14.5f, 15.0f, 23.2f, 30.9f, 31.3f, 32.9f, 35.1f, 34.3f
//				};
//
//			background(0);
//			// turn on the lights so that we see shading on the 3D objects
//			lights();
//			// move to the center of the sketch before we draw our graph
//			translate(width / 2, height / 2);
//			// Tilt about 70 degrees on the X axis - like tilting a frame on the
//			// wall so that it's on a table
//			rotateX(1.2f);
//			// Now, spin around the Z axis as the mouse moves. Like spinning
//			// that frame on the table around its center
//			rotateZ(map(mouseX, 0, width, 0, TWO_PI));
//
//			for (int i = 0; i < numbers.length; i++) {
//				// calculate the amount of green in the colour by mapping the
//				// number to 255 (255 red &amp; 255 green = yellow)
//				float c = map(numbers[i], min(numbers), max(numbers), 0, 255);
//				fill(255, c, 0);
//				// calculate the height of the bar by mapping the number to the
//				// half the width of the screen minus 50 pixels
//				float w = map(numbers[i], 0, max(numbers), 0, width / 2 - 50);
//				// move out 200 pixels from the center
//				pushMatrix();
//				translate(200, 0);
//				box(20, 20, w);
//				popMatrix();
//				// after we draw each bar, turn the sketch a bit
//				rotate(TWO_PI / numbers.length);
//			}

			// custom using gicentreUtils

//			size(300,200);
//
//			BarChart barChart = new BarChart(this);
//			barChart.setData(new float[] { 0.76f, 0.24f, 0.39f, 0.18f, 0.20f });
//
//			// Axis scaling
//			barChart.setMinValue(0);
//			barChart.setMaxValue(1);
//
//			barChart.showValueAxis(true);
//			barChart.showCategoryAxis(true);
		}
	}
}
