package org.ideasmashup.specialtactics;

import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gicentre.utils.move.ZoomPan;
import org.gicentre.utils.move.ZoomPanListener;
import org.gicentre.utils.multisketch.EmbeddedSketch;
import org.gicentre.utils.stat.BarChart;
import org.ideasmashup.specialtactics.brains.Brain;
import org.ideasmashup.specialtactics.brains.BrainListener;
import org.ideasmashup.specialtactics.managers.Agents;
import org.ideasmashup.specialtactics.managers.Needs;
import org.ideasmashup.specialtactics.managers.Needs.Types;
import org.ideasmashup.specialtactics.managers.Producers;
import org.ideasmashup.specialtactics.managers.Resources;
import org.ideasmashup.specialtactics.managers.Supplies;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

public class GUI implements BrainListener {

	private static final int WIDTH = 600;
	private static final int HEIGHT = 900;

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
		frame.setLocation(new Point(10, 50));
//		frame.setResizable(false);
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

		// ------------------ Sketch-wide variables ---------------------------

		// AI managers objects
		Needs needs = Needs.getInstance();
		Producers producers = Producers.getInstance();

		// giCentre classes
		PFont titleFont, smallFont, tinyFont;
		BarChart bcPro, bcRes, bcSup, bcUni;
		ZoomPan zoomer;
		PVector mousePos;
		NumberFormat formatter = new DecimalFormat("#.0");

		// real-time variations charts values
		LinkedList<Float> nRes = new LinkedList<Float>();
		LinkedList<Float> nSup = new LinkedList<Float>();
		LinkedList<Float> nUni = new LinkedList<Float>();
		LinkedList<Float> nPro = new LinkedList<Float>();

		// aggregated summary chart
		BarChart bcMaster;
		String[] masterLabels = new String[] { "Needs (res)", "Need (supply)", "Need (units)",
				"Producers", "Consumers" };
		float[] masterValues = new float[] { 0, 0, 0, 0, 0 };

		static final int PLOTS_MAX = 16;

		// ------------------ Initialisation ----------------------------------

		@Override
		public void setup() {
			// don't remove try catch otherwise errors not shown!!
			try {
				size(GUI.WIDTH, GUI.HEIGHT);

				// /////////////// Setup sketch ///////////////////////////
				smooth();
				//noLoop();
				frameRate(16);

				zoomer = new ZoomPan(this);
				zoomer.allowZoomButton(false);

				// Monitor end of zoom/pan events.
				zoomer.addZoomPanListener(new ZoomListener());

				titleFont = loadFont("Helvetica-22.vlw");
				smallFont = loadFont("Helvetica-12.vlw");
				tinyFont = createDefaultFont(8);
				textFont(smallFont);

				bcMaster = initMasterChart();
				bcPro = initChart(nPro);
				bcRes = initChart(nRes);
				bcSup = initChart(nSup);
				bcUni = initChart(nUni);
				// ///////////////////////////////////////////////////////
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ----------------- Processing draw ----------------------------------

		@Override
		public void draw() {
			// example: http://www.sebastianoliva.com/en/en/2010/05/using-a-processing-sketch-as-a-java-component/trackback/
			// example: http://blog.blprnt.com/blog/blprnt/tutorial-processing-javascript-and-data-visualization

			// don't remove try catch otherwise errors not shown!!
			try {
				// /////////////// Draw sketch ///////////////////////////

				// add data to internal collections
				float fRes = masterValues[0] = needs.getNeedsCount(Types.RESOURCES);
				float fSup = masterValues[1] = needs.getNeedsCount(Types.SUPPLY);
				float fUni = masterValues[2] = needs.getNeedsCount(Types.UNIT);
				float fPro = masterValues[3] = producers.getProducersCount();
				float fCon = masterValues[4] = producers.getConsumersCount();
				float fAge = Agents.getInstance().getAgentsCount();

				float fRMin = Resources.getInstance().getReservedMinerals();
				float fRGas = Resources.getInstance().getReservedGas();
				float fRSup = Supplies.getInstance().getReservedSupply();

				nPro.addFirst(fPro);
				if (nPro.size() > PLOTS_MAX) nPro.removeLast();

				nRes.addFirst(fRes);
				if (nRes.size() > PLOTS_MAX) nRes.removeLast();

				nSup.addFirst(fSup);
				if (nSup.size() > PLOTS_MAX) nSup.removeLast();

				nUni.addFirst(fUni);
				if (nUni.size() > PLOTS_MAX) nUni.removeLast();

				// activate zooming
				pushMatrix();
				zoomer.transform();

				// start drawing everything
				background(0);
				fill(120);

				// plot master chart
				plotMasterChart(90, 130);

				// plot all data
				plotChart(bcPro, "Producers", nPro, 250, 100);
				plotChart(bcRes, "Needs (min/gas)", nRes, 400, 100);
				plotChart(bcSup, "Needs (supply)", nSup, 550, 100);
				plotChart(bcUni, "Needs (units)", nUni, 700, 100);

				// add titles
				textFont(titleFont);
				text("Overview of internal queues", 10, 30);
				textFont(smallFont);
				text("Total items per queue", 10, 50);

				// non-zoomed stuff here
				popMatrix();
				textAlign(LEFT, BOTTOM);

				// Get the mouse position taking into account any zooming and
				// panning.
				mousePos = zoomer.getMouseCoord();

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
			else if (key == 'r') {
				zoomer.reset();
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

		class ZoomListener implements ZoomPanListener {
			@Override
			public void panEnded() {
				//println("Panning stopped");
			}

			@Override
			public void zoomEnded() {
				//println("Zooming stopped");
			}
		}

		// -------------------------- Private methods -------------------------

		private BarChart initMasterChart() {
			BarChart bc = new BarChart(this);

			bc.setBarColour(color(100, 100, 200, 100));
			bc.setBarGap(2);
			bc.setValueFormat("###,###");
			bc.showValueAxis(true);
			bc.setData(masterValues);
			bc.setBarLabels(masterLabels);
			bc.showCategoryAxis(true);

			return bc;
		}

		private void plotMasterChart(int top, int height) {
			bcMaster.setData(masterValues);

			stroke(0);
			textFont(smallFont);
			textFont(tinyFont);
			bcMaster.draw(10, top + 20, width - 10, height);
		}

		private BarChart initChart(List<Float> data) {
			BarChart bc = new BarChart(this);

			bc.setBarColour(color(200, 80, 80, 100));
			bc.setBarGap(2);
			bc.setValueFormat("###,###");
			bc.showValueAxis(true);
			bc.showCategoryAxis(false);

			return bc;
		}

		private void plotChart(BarChart bc, String title, List<Float> data, int top, int height) {

			bc.setData(values(data));
//			bc.setBarLabels(labels(data));

			stroke(0);
			textFont(smallFont);
			text(title, 10, top + textAscent());
			textFont(tinyFont);
			bc.draw(10, top + 20, width-20, height);
		}

		private float[] values(List<Float> list) {
			float[] floats = new float[list.size()];
			int i = 0;

			for (Float f : list) {
				floats[i++] = (f != null ? f : Float.NaN);
			}

			return floats;
		}

		private String[] labels(List<Float> list) {
			String[] strings = new String[list.size()];
			int i = 0;

			for (Float f : list) {
				strings[i++] = (f != null ? Float.toString(f) : "");
			}

			return strings;
		}
	}
}
