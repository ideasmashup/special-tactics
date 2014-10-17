package org.ideasmashup.specialtactics;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.ideasmashup.specialtactics.brains.Brain;
import org.ideasmashup.specialtactics.managers.Tiles;
import org.ideasmashup.specialtactics.managers.Tiles.EditTool;
import org.ideasmashup.specialtactics.managers.Tiles.Specs;
import org.ideasmashup.specialtactics.managers.Units;
import org.ideasmashup.specialtactics.tiles.Tile;

import bwapi.TilePosition;
import bwapi.UnitType;

public class GUI extends GuiWindow {

	public static final int WIDTH = 600;
	public static final int HEIGHT = 200;

	private final Brain brain;

	private JPanel panel;

	private GuiWindow wndSpeed;
	private GuiWindow wndStats;
	private final GuiWindow wndBGrid;

	public GUI(Brain brain) {
		super("AI Dashboard");
		this.wndSpeed = null;
		this.wndStats = null;
		this.wndBGrid = null;

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

		button = new JButton("Buildings Grid");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tiles tiles = Tiles.getInstance();
				tiles.setMode(Tiles.Mode.build);
				/*if (wndBGrid == null) {
					wndBGrid = new GuiBGrid();
					wndBGrid.show(true);
				}*/
				// force editing cursor to be on visible screen cell
				TilePosition cell = AI.getPlayer().getStartLocation();
				tiles.buildX = cell.getX();
				tiles.buildY = cell.getY();
			}
		});
		button.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				int code = ke.getKeyCode();

				Tiles tiles = Tiles.getInstance();

				if (code == KeyEvent.VK_LEFT) {
					// FIXME horrible direct access for demo hackathon
					// move cursor left
					tiles.buildX = Math.max(0, tiles.buildX - 1);
				}
				else if (code == KeyEvent.VK_RIGHT) {
					// FIXME horrible direct access for demo hackathon
					// move cursor right
					tiles.buildX = Math.min(tiles.getBuildColsCount() - 1, tiles.buildX + 1);
				}
				if (code == KeyEvent.VK_UP) {
					// FIXME horrible direct access for demo hackathon
					// move cursor up
					tiles.buildY = Math.max(0, tiles.buildY - 1);
				}
				else if (code == KeyEvent.VK_DOWN) {
					// FIXME horrible direct access for demo hackathon
					// move cursor down
					tiles.buildY = Math.min(tiles.getBuildRowsCount() - 1, tiles.buildY + 1);
				}
				else if (code == KeyEvent.VK_SHIFT) {
					// change edit tool
					switch(tiles.edittool) {
						default:
						case buildable:
							tiles.edittool = EditTool.production;
							AI.say("Tiles editing : production tiles");
							break;
						case production:
							tiles.edittool = EditTool.supply;
							AI.say("Tiles editing : supply tiles");
							break;
						case supply:
							tiles.edittool = EditTool.buildable;
							AI.say("Tiles editing : buildable tiles");
							break;
					}
				}
				else if (code == KeyEvent.VK_SPACE) {
					// edit selected tile according to selected tool
					Tile tile = tiles.getBuildTile(new TilePosition(tiles.buildX, tiles.buildY));

					switch(tiles.edittool) {
						default:
						case buildable:
							Boolean value = !(Boolean) tile.getSpecs(Specs.BUILDABLE);
							tile.setSpecs(Specs.BUILDABLE, value);
							AI.say("Tiles editing : tile's <buildable> set to "+ value);
							break;
						case production:
							// fetch all build types authorized for this tile
							UnitType[] _types = (UnitType[]) tile.getSpecs(Specs.BUILD_TYPES);
							LinkedList<UnitType> types = new LinkedList<UnitType>();
							types.addAll(Arrays.asList(_types));

							// remove or add production building
							UnitType type = Units.Types.PROD_T1.getUnitType();
							if (types.contains(type)) {
								types.remove(type);
							}
							else {
								types.add(type);
							}

							tile.setSpecs(Specs.BUILD_TYPES, types.toArray(new UnitType[0]));
							AI.say("Tiles editing : tile's <build_types> set to "+ Arrays.toString(_types));
							break;
						case supply:
							// fetch all build types authorized for this tile
							UnitType[] _types2 = (UnitType[]) tile.getSpecs(Specs.BUILD_TYPES);
							LinkedList<UnitType> types2 = new LinkedList<UnitType>();
							types2.addAll(Arrays.asList(_types2));

							// remove or add production building
							UnitType type2 = Units.Types.SUPPLY.getUnitType();
							if (types2.contains(type2)) {
								types2.remove(type2);
							}
							else {
								types2.add(type2);
							}

							tile.setSpecs(Specs.BUILD_TYPES, types2.toArray(new UnitType[0]));
							AI.say("Tiles editing : tile's <build_types> set to "+ Arrays.toString(_types2));
							break;
					}
				}
			}
		});
		panel.add(button);

		button = new JButton("Units Grid");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tiles.getInstance().setMode(Tiles.Mode.units);
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
