package org.ideasmashup.specialtactics;

import java.awt.BorderLayout;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ideasmashup.specialtactics.agents.AsyncOrders;

public class GuiSpeed extends GuiWindow {

	private JFrame gameControls;
	private FrameRate currentFrameRate;

	public GuiSpeed() {
		super("Game speed");
	}

	@Override
	protected void buildLayout() {
		buildGameControlToolbar();
		super.buildLayout();
	}

	@Override
	protected void plugBehavior() {
		gameControls.setAlwaysOnTop(true);
		gameControls.setSize(600, 100);
		gameControls.setResizable(false);
		gameControls.setLocation(620, 50 + GUI.HEIGHT - 110);
	}

	private void buildGameControlToolbar() {
		gameControls = new JFrame("Game controls");
		gameControls.setType(Type.UTILITY);
		final JPanel pContent = new JPanel();
		pContent.setLayout(new BoxLayout(pContent, BoxLayout.PAGE_AXIS));
		final JPanel pSpeed = new JPanel();
		pSpeed.setLayout(new BorderLayout(10, 0));
		pSpeed.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Speed"), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		final JButton bSpeedMinus = new JButton("<");
		final JLabel lSpeed = new JLabel();
		lSpeed.setHorizontalAlignment(JLabel.CENTER);
		final JButton bSpeedPlus = new JButton(">");
		final ActionListener actionSpeed = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final List<FrameRate> rates = Arrays.asList(FrameRate.values());
				int currentIndex = rates.indexOf(currentFrameRate);
				currentFrameRate = rates.get(e.getSource() == bSpeedMinus ? --currentIndex : ++currentIndex);
				final int index = currentIndex;
				final int speed = currentFrameRate.getLocalSpeed();
				final String name = currentFrameRate.name();
				AsyncOrders.getInstance().addOrder(new Runnable() {
					@Override
					public void run() {
						AI.getGame().setLocalSpeed(speed);
						bSpeedMinus.setEnabled(index > 0);
						bSpeedPlus.setEnabled(index < rates.size() - 1);
						lSpeed.setText(name.replace("_", " "));
					}
				});
			}
		};
		bSpeedMinus.addActionListener(actionSpeed);
		bSpeedPlus.addActionListener(actionSpeed);
		pSpeed.add(bSpeedMinus, BorderLayout.WEST);
		pSpeed.add(lSpeed, BorderLayout.CENTER);
		pSpeed.add(bSpeedPlus, BorderLayout.EAST);
		pContent.add(pSpeed);
		gameControls.setContentPane(pContent);
		currentFrameRate = FrameRate.FASTEST;
		lSpeed.setText(currentFrameRate.name().replace("_", " "));
	}

	@Override
	public void show(boolean visible) {
		gameControls.setVisible(visible);
	}
}