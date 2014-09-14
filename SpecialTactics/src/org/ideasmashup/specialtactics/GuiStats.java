package org.ideasmashup.specialtactics;

import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
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

import bwapi.Player;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

public class GuiStats extends GuiWindow implements BrainListener {

	private final Brain brain;
	private final UpdatesThread updater;
	private boolean updating;

	private PApplet sketch;
	private JPanel panel;

	public GuiStats(Brain brain) {
		super("Stats & Charts");

		this.brain = brain;
		this.updater = new UpdatesThread();

		this.updating = true;

		initAI();

		// attach itself to brain events
		brain.addListener(this);

		// run sketch
		sketch.init();
	}

	@Override
	protected void buildLayout() {
		// create Processing "sketch" (see docs)
		sketch = new ProcessingApplet();

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setSize(GUI.WIDTH, GUI.HEIGHT);
		panel.add(sketch);

		frame.add(panel);

		super.buildLayout();
	}

	@Override
	protected void plugBehavior() {
		frame.setAlwaysOnTop(true);
		frame.setSize(GUI.WIDTH, GUI.HEIGHT);
		frame.setResizable(false);
		frame.setLocation(new Point(10, 50));
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				updating = false;
			}
		});
	}

	private void initAI() {
		// start thread that updates AI details to display items like queues...
		updater.start();
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
		BarChart bcPro, bcRes, bcSup, bcUni, bcSup2, bcUMin, bcUGas;
		ZoomPan zoomer;
		PVector mousePos;
		NumberFormat formatter = new DecimalFormat("#.0");

		// real-time variations charts values
		LinkedList<Float> nRes = new LinkedList<Float>();
		LinkedList<Float> nSup = new LinkedList<Float>();
		LinkedList<Float> nSup2 = new LinkedList<Float>();
		LinkedList<Float> nUni = new LinkedList<Float>();
		LinkedList<Float> nPro = new LinkedList<Float>();
		LinkedList<Float> nUMin = new LinkedList<Float>();
		LinkedList<Float> nUGas = new LinkedList<Float>();

		// aggregated summary chart
		BarChart bcMaster;
		String[] masterLabels = new String[] { "Needs (res)", "Need (supply)", "Need (units)",
				"Producers", "Consumers" };
		float[] masterValues = new float[] { 0, 0, 0, 0, 0 };

		static final int PLOTS_MAX = 50;

		// ------------------ Initialisation ----------------------------------

		@Override
		public void setup() {
			// don't remove try catch otherwise errors not shown!!
			try {
				size(GUI.WIDTH, GUI.HEIGHT);

				// /////////////// Setup sketch ///////////////////////////
				smooth();
				// noLoop();
				frameRate(8);

				zoomer = new ZoomPan(this);
				zoomer.allowZoomButton(false);

				// Monitor end of zoom/pan events.
				zoomer.addZoomPanListener(new ZoomListener());

				titleFont = loadFont("Helvetica-22.vlw");
				smallFont = loadFont("Helvetica-12.vlw");
				tinyFont = createDefaultFont(8);
				textFont(smallFont);

				bcMaster = initMasterChart();
				bcPro = initChart(nPro, color(200, 80, 80, 100));
				bcSup2 = initChart(nSup2, color(200, 100, 10, 100));
				bcRes = initChart(nRes, color(10, 100, 255, 100));
				bcSup = initChart(nSup, color(10, 200, 80, 100));
				bcUni = initChart(nUni, color(100, 100, 100, 100));
				bcUMin = initChart(nUMin, color(0, 0, 100, 100));
				bcUGas = initChart(nUGas, color(0, 100, 0, 100));
				// ///////////////////////////////////////////////////////
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		// ----------------- Processing draw ----------------------------------

		@Override
		public void draw() {
			// example:
			// http://www.sebastianoliva.com/en/en/2010/05/using-a-processing-sketch-as-a-java-component/trackback/
			// example:
			// http://blog.blprnt.com/blog/blprnt/tutorial-processing-javascript-and-data-visualization

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
				float fSup2 = Supplies.getInstance().getSuppliersCount();
				float fUMin = Resources.getInstance().getUnusedMinerals();
				float fUGas = Resources.getInstance().getUnusedGas();

				nPro.addFirst(fAge);
				if (nPro.size() > PLOTS_MAX) nPro.removeLast();

				nSup2.addFirst(fSup2);
				if (nSup2.size() > PLOTS_MAX) nSup2.removeLast();

				nRes.addFirst(fRMin);
				if (nRes.size() > PLOTS_MAX) nRes.removeLast();

				nSup.addFirst(fRGas);
				if (nSup.size() > PLOTS_MAX) nSup.removeLast();

				nUni.addFirst(fRSup);
				if (nUni.size() > PLOTS_MAX) nUni.removeLast();

				nUMin.addFirst(fUMin);
				if (nUMin.size() > PLOTS_MAX) nUMin.removeLast();

				nUGas.addFirst(fUGas);
				if (nUGas.size() > PLOTS_MAX) nUGas.removeLast();

				// activate zooming
				pushMatrix();
				zoomer.transform();

				// start drawing everything
				background(0);
				fill(120);

				// plot master chart
				plotMasterChart("Collections sizes", 90, 130);

				// plot all data
				plotChart(bcPro, "Total active agents : "+ fAge, nPro, 300, 40);
				plotChart(bcSup2, "Total active suppliers : "+ fSup2, nSup2, 360, 40);
				plotChart(bcRes, "Reserved minerals : "+ fRMin, nRes, 420, 40);
				plotChart(bcUMin, "Unused minerals : "+ fUMin, nUMin, 480, 40);
				plotChart(bcSup, "Reserved gas : "+ fRGas, nSup, 540, 40);
				plotChart(bcUGas, "Unused gas : "+ fUGas, nUGas, 600, 40);
				plotChart(bcUni, "Reserved supply : "+ fRSup, nUni, 660, 40);

				// add titles
				textFont(titleFont);
				text("Overview of internal data", 10, 30);
				textFont(smallFont);
				text("Current amounts stored", 10, 50);

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

		private void plotMasterChart(String title, int top, int height) {
			bcMaster.setData(masterValues);

			stroke(0);
			textFont(smallFont);
			text(title, 10, top + textAscent());
			textFont(tinyFont);
			bcMaster.draw(10, top + 20, width - 10, height);
		}

		private BarChart initChart(List<Float> data, int color) {
			BarChart bc = new BarChart(this);

			bc.setBarColour(color);
			bc.setBarGap(2);
			bc.setValueFormat("###,###");
			bc.showValueAxis(true);
			bc.showCategoryAxis(false);

			return bc;
		}

		private void plotChart(BarChart bc, String title, List<Float> data, int top, int height) {

			bc.setData(values(data));
			// bc.setBarLabels(labels(data));

			stroke(0);
			textFont(smallFont);
			text(title, 10, top + textAscent() + 10);
			textFont(tinyFont);
			bc.draw(10, top + 20, width - 20, height);
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

	@Override
	public void onSendText(String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiveText(Player player, String text) {
		// TODO Auto-generated method stub

	}
}
