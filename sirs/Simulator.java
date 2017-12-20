package sirs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Scrollbar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * UI for SIRS simulation. Can be repurposed generally for any lattice model with sufficient refactoring.
 * @author User
 *
 */
public class Simulator extends JFrame implements WindowListener{
	private static final long serialVersionUID = 8959478696279589941L;
	
	private JTabbedPane tabs;
	private JPanel sirsPanel;
	
	private JButton toggleButton;
	private JButton settingsButton;
	private JButton resetButton;
	private JButton resetColorsButton;
	private JButton updateLatticeButton;
	private JCheckBox automaticSettingsUpdateBox;
	private JCheckBox immunityCheckBox;
	private JCheckBox logBox;
	private JCheckBox repaintBox;
	private JCheckBox circularCellBox;
	private JCheckBox rectangularCellBox;
	private ProportionBar populationBar;

	private BufferedWriter buffer;
	private FileWriter writer;
	private File logFile;
	private String logFileName = "sirslog";

	private boolean running = false;	
	private boolean automaticSettingsUpdate = true;
	private boolean enableImmunity = false;
	private boolean enableLogging;
	private boolean previousLoggingState;
	private boolean repaintOnUpdate = true;

	private double infectionProbability;
	private double relapseProbability;
	private double recoveryProbability;
	private int immuneProportion;
	private boolean ready = false;

	private SIRSModel model;
	private ModelComponent modelComponent;
	private int width = 50;
	private int height = 50;
	private int size;
	private int updatesPerTick = 100;
	private long period = 16; //~60fps

	private int infectedCount;
	private int recoveredCount;
	private int susceptibleCount;
	private int immuneCount;

	private JScrollBar infectionBar;
	private JScrollBar relapseBar;
	private JScrollBar cureBar;
	private JScrollBar tickBar;
	private JScrollBar sizeBar;
	private JScrollBar immunityBar;

	private ColorBlock infectedColorChooser;
	private ColorBlock recoveredColorChooser;
	private ColorBlock susceptibleColorChooser;
	private ColorBlock immuneColorChooser;

	private Color infectedColor;
	private Color recoveredColor;
	private Color susceptibleColor;
	private Color immuneColor;

	private JLabel infectionLabel;
	private JLabel relapseLabel;
	private JLabel cureLabel;
	private JLabel settingsUpdateLabel;
	private JLabel tickLabel;
	private JLabel infectedColorLabel;
	private JLabel recoveredColorLabel;
	private JLabel susceptibleColorLabel;
	private JLabel immuneColorLabel;
	private JLabel sizeLabel;
	private JLabel immunityLabel;
	private JLabel enableImmuneLabel;
	private JLabel logLabel;
	private JLabel tickCountLabel;
	private JLabel repaintLabel;
	private JLabel circularCellBoxLabel;
	private JLabel rectangularCellBoxLabel;

	private Point infectionBarLocation;
	private Point relapseBarLocation;
	private Point cureBarLocation;
	private Point tickBarLocation;
	private Point sizeBarLocation;
	private Point immunityBarLocation;
	private Point immunityCheckBoxLocation;
	private Point infectedColorChooserLocation;
	private Point recoveredColorChooserLocation;
	private Point susceptibleColorChooserLocation;
	private Point immuneColorChooserLocation;
	private Point infectedColorLabelLocation;
	private Point recoveredColorLabelLocation;
	private Point susceptibleColorLabelLocation;
	private Point immuneColorLabelLocation;
	private Point infectionLabelLocation;
	private Point relapseLabelLocation;
	private Point cureLabelLocation;
	private Point tickLabelLocation;
	private Point sizeLabelLocation;
	private Point logLabelLocation;
	private Point modelLocation;
	private Point toggleButtonLocation;
	private Point settingsButtonLocation;
	private Point resetButtonLocation;
	private Point automaticSettingsUpdateBoxLocation;
	private Point logBoxLocation;
	private Point circularCellBoxLocation;
	private Point rectangularCellBoxLocation;
	private Point settingsUpdateLabelLocation;
	private Point resetColorsButtonLocation;
	private Point updateLatticeButtonLocation;
	private Point immunityLabelLocation;
	private Point enableImmuneLabelLocation;
	private Point populationBarLocation;
	private Point tickCountLabelLocation;
	private Point repaintBoxLocation;
	private Point repaintLabelLocation;
	private Point circularCellBoxLabelLocation;
	private Point rectangularCellBoxLabelLocation;

	private Timer modelTimer;

	private static final Dimension DEFAULT_SIZE = new Dimension(1600,900);

	public Simulator() {
		super();
		setSize(DEFAULT_SIZE);
		initialise();
		setVisible(true);
	}

	private void initialise() {
		initTabbedPanel();
		initPanels();
		setLayout(null);
		initLocations();
		initButtons();
		initLabels();
		initScrollBars();
		initModel();
		initTimer();
		initCheckBoxes();
		initColorChoosers();
		initPopulationBar();
		addWindowListener(this);
		
		//add tabs
		tabs.add(sirsPanel,"SIRS");
		
		//tabs end
		setContentPane(tabs);
		//setResizeable(false);
		resizeToFitScreen();
		ready = true;
	}


	private void initPanels() {
		sirsPanel = new JPanel();
		sirsPanel.setLayout(null);
//		isingPanel = new JPanel();
//		isingPanel.setLayout(null);
	}

	private void initTabbedPanel() {
		tabs = new JTabbedPane();
	}

	/**
	 * Opens the log file for writing simulation data out.
	 */
	private void initLogger() {
		int append = 0;
		logFile = new File(logFileName + append + ".csv");
		while (logFile.exists()) {
			append++;
			logFile = new File(logFileName + append + ".csv");
		}
		try {
			writer = new FileWriter(logFile);
			buffer = new BufferedWriter(writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes data out to the log file for the current step.
	 */
	private void logData() {
		String out = infectedCount + "," + recoveredCount + "," + susceptibleCount + "," + immuneCount +"\r\n";
		try {
			buffer.write(out);
			buffer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeLogger() {
		try {
			buffer.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the population bar, used to track the number of infected, susceptible and cured cells.
	 */
	private void initPopulationBar() {
		countPopulation();
		double[] populationValues = {infectedCount,recoveredCount,susceptibleCount,immuneCount};
		populationBar = new ProportionBar(populationValues);
		populationBar.setSize(600,50);
		populationBar.setLocation(populationBarLocation);
		populationBar.setVisible(true);
		sirsPanel.add(populationBar);
	}
	
	/**
	 * Resizes components within the Simulation panel. Not perfect, but it gets the job done.
	 * @param scale
	 */
	public void scale(double scale) {
		Component[] components = sirsPanel.getComponents();
		
		for (Component c : components) {
			Point location = c.getLocation();
			int x = (int) (location.x * scale);
			int y = (int) (location.y * scale);
			int width = (int) (c.getWidth() * scale);
			int height = (int) (c.getHeight() * scale);
			
			if (c instanceof JLabel) {
				Font labelFont = c.getFont();
				int newFontSize = (int)(Math.floor(labelFont.getSize() * scale));
				c.setFont(new Font(labelFont.getName(), Font.PLAIN, newFontSize));
			}
			
			c.setLocation(x,y);
			c.setSize(width,height);
		}
	}

	/**
	 * Initialize the Color Choosers for infected, susceptible and cured cell colors.
	 */
	private void initColorChoosers() {
		Dimension chooserSize = new Dimension(40,40);
		infectedColor = ModelComponent.DEFAULT_INFECTED_COLOR;
		recoveredColor = ModelComponent.DEFAULT_RECOVERED_COLOR;
		susceptibleColor = ModelComponent.DEFAULT_SUSCEPTIBLE_COLOR;
		immuneColor = ModelComponent.DEFAULT_IMMUNE_COLOR;

		infectedColorChooser = new ColorBlock(infectedColor,this);
		infectedColorChooser.setSize(chooserSize);
		infectedColorChooser.setLocation(infectedColorChooserLocation);
		sirsPanel.add(infectedColorChooser);

		recoveredColorChooser = new ColorBlock(recoveredColor,this);
		recoveredColorChooser.setSize(chooserSize);
		recoveredColorChooser.setLocation(recoveredColorChooserLocation);
		sirsPanel.add(recoveredColorChooser);

		susceptibleColorChooser = new ColorBlock(susceptibleColor,this);
		susceptibleColorChooser.setSize(chooserSize);
		susceptibleColorChooser.setLocation(susceptibleColorChooserLocation);
		sirsPanel.add(susceptibleColorChooser);

		immuneColorChooser = new ColorBlock(immuneColor,this);
		immuneColorChooser.setSize(chooserSize);
		immuneColorChooser.setLocation(immuneColorChooserLocation);
		sirsPanel.add(immuneColorChooser);
	}

	/**
	 * Count the number of infected, susceptible and cured cells.
	 */
	private void countPopulation() {
		State[][] agents = model.getAgents();
		int width = model.getWidth();
		int height = model.getHeight();

		infectedCount = 0;
		recoveredCount = 0;
		susceptibleCount = 0;
		immuneCount = 0;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				State agent = agents[i][j];

				if (agent == State.INFECTED) {
					infectedCount++;
				} else if (agent == State.RECOVERED) {
					recoveredCount++;
				} else if (agent == State.SUSCEPTIBLE) {
					susceptibleCount++;
				} else if (agent == State.IMMUNE) {
					immuneCount++;
				} 
			}
		}
	}

	/**
	 * Updates the population bar with the number of infected, susceptible and cured cells in the current step.
	 */
	private void updatePopulationBar() {
		countPopulation();
		double[] populationValues = {infectedCount,recoveredCount,susceptibleCount,immuneCount};
		Color[] colors = {infectedColor,recoveredColor,susceptibleColor,immuneColor};
		populationBar.setValues(populationValues);
		populationBar.setColors(colors);
		populationBar.repaint();

	}

	private void initTimer() {
		modelTimer = new Timer();
	}

	/**
	 * Initializes checkboxes for the simulation settings.
	 */
	private void initCheckBoxes() {
		automaticSettingsUpdateBox = new JCheckBox();
		automaticSettingsUpdateBox.setSize(20,20);
		automaticSettingsUpdateBox.setLocation(automaticSettingsUpdateBoxLocation);
		automaticSettingsUpdateBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				automaticSettingsUpdate = automaticSettingsUpdateBox.isSelected();
				if (automaticSettingsUpdate) {
					updateSettings();
				}
			}
		});
		automaticSettingsUpdateBox.setSelected(automaticSettingsUpdate);
		sirsPanel.add(automaticSettingsUpdateBox);

		immunityCheckBox = new JCheckBox();
		immunityCheckBox.setSize(20,20);
		immunityCheckBox.setLocation(immunityCheckBoxLocation);
		immunityCheckBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				enableImmunity = immunityCheckBox.isSelected();
			}
		});
		immunityCheckBox.setSelected(enableImmunity);
		sirsPanel.add(immunityCheckBox);

		logBox = new JCheckBox();
		logBox.setSize(20,20);
		logBox.setLocation(logBoxLocation);
		logBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				previousLoggingState = enableLogging;
				enableLogging = logBox.isSelected();

				if (previousLoggingState != enableLogging) {
					//check to open or close logger
					if (enableLogging) {
						initLogger();
					} else {
						closeLogger();
					}
				}
			}
		});
		logBox.setSelected(enableImmunity);
		sirsPanel.add(logBox);

		repaintBox = new JCheckBox();
		repaintBox.setSize(20,20);
		repaintBox.setLocation(repaintBoxLocation);
		repaintBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				repaintOnUpdate = repaintBox.isSelected();
			}
		});
		repaintBox.setSelected(repaintOnUpdate);
		sirsPanel.add(repaintBox);
		
		circularCellBox = new JCheckBox();
		circularCellBox.setSize(30,30);
		circularCellBox.setLocation(circularCellBoxLocation);
		circularCellBox.setSelected(false);
		sirsPanel.add(circularCellBox);
		
		rectangularCellBox = new JCheckBox();
		rectangularCellBox.setSize(30,30);
		rectangularCellBox.setLocation(rectangularCellBoxLocation);
		rectangularCellBox.setSelected(true);
		sirsPanel.add(rectangularCellBox);
		
		circularCellBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				rectangularCellBox.setSelected(!circularCellBox.isSelected());
				updateCellShape();
			}
		});
		
		rectangularCellBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				circularCellBox.setSelected(!rectangularCellBox.isSelected());
				updateCellShape();
			}
		});
	}


	/**
	 * Begin/resume the simulation.
	 */
	public void startTimer() {
		modelTimer = new Timer();
		modelTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateModel();
				updateTickCountLabel();
			}

		},period,period);
	}

	/**
	 * Update the UI to show the number of steps the simulation has been running for.
	 */
	protected void updateTickCountLabel() {
		tickCountLabel.setText("Update Count: " + model.getTicks());
	}

	/**
	 * Runs a number of sequential updates on the simulation model based on the simulation settings.
	 * Also logs data if the settings if told to.
	 */
	protected void updateModel() {
		for (int i = 0; i < updatesPerTick; i++) {
			model.updateSequential();
		}
		updatePopulationBar();

		if (enableLogging) {
			logData();
		}

		if (repaintOnUpdate) {
			modelComponent.repaint();
		}
	}

	/**
	 * Pause the simulation.
	 */
	public void stopTimer() {
		modelTimer.cancel();
	}

	/**
	 * Initializethe model with the settings given.
	 */
	private void initModel() {
		model = new SIRSModel(width,height,0);
		modelComponent = new ModelComponent(model);
		modelComponent.setSize(600,600);
		modelComponent.setLocation(modelLocation);
		sirsPanel.add(modelComponent);

	}

	/**
	 * Generate a new simulation model based on the settings given.
	 */
	private void updateLatticeModel() {
		width = size;
		height = size;
		if (enableImmunity) {
			model.setImmuneProportion((double)immuneProportion / 100);
		} else {
			model.setImmuneProportion(0);
		}
		model.setSize(size);
		reset();
	}

	/**
	 * Initialize labels in the UI.
	 */
	private void initLabels() {
		infectionLabel = new JLabel("Infection: " + infectionProbability);
		infectionLabel.setSize(100,30);
		infectionLabel.setLocation(infectionLabelLocation);
		sirsPanel.add(infectionLabel);

		relapseLabel = new JLabel("Relapse: " + relapseProbability);
		relapseLabel.setSize(100,30);
		relapseLabel.setLocation(relapseLabelLocation);
		sirsPanel.add(relapseLabel);

		cureLabel = new JLabel("Cure: " + recoveryProbability);
		cureLabel.setSize(100,30);
		cureLabel.setLocation(cureLabelLocation);
		sirsPanel.add(cureLabel);

		tickLabel = new JLabel(updatesPerTick + " Updates per tick");
		tickLabel.setSize(200,30);
		tickLabel.setLocation(tickLabelLocation);
		sirsPanel.add(tickLabel);

		settingsUpdateLabel = new JLabel("Automatically update settings:");
		settingsUpdateLabel.setSize(300,30);
		settingsUpdateLabel.setLocation(settingsUpdateLabelLocation);
		sirsPanel.add(settingsUpdateLabel);

		infectedColorLabel = new JLabel("Infected:");
		infectedColorLabel.setSize(300,30);
		infectedColorLabel.setLocation(infectedColorLabelLocation);
		sirsPanel.add(infectedColorLabel);

		recoveredColorLabel = new JLabel("Recovered:");
		recoveredColorLabel.setSize(300,30);
		recoveredColorLabel.setLocation(recoveredColorLabelLocation);
		sirsPanel.add(recoveredColorLabel);

		susceptibleColorLabel = new JLabel("Susceptible:");
		susceptibleColorLabel.setSize(300,30);
		susceptibleColorLabel.setLocation(susceptibleColorLabelLocation);
		sirsPanel.add(susceptibleColorLabel);

		immuneColorLabel = new JLabel("Immune:");
		immuneColorLabel.setSize(300,30);
		immuneColorLabel.setLocation(immuneColorLabelLocation);
		sirsPanel.add(immuneColorLabel);

		sizeLabel = new JLabel("Lattice Size: " + width);
		sizeLabel.setSize(300,30);
		sizeLabel.setLocation(sizeLabelLocation);
		sirsPanel.add(sizeLabel);

		immunityLabel = new JLabel("Immune proportion: " + immuneProportion);
		immunityLabel.setSize(300,30);
		immunityLabel.setLocation(immunityLabelLocation);
		sirsPanel.add(immunityLabel);

		enableImmuneLabel = new JLabel("Enable Immunity:");
		enableImmuneLabel.setSize(300,30);
		enableImmuneLabel.setLocation(enableImmuneLabelLocation);
		sirsPanel.add(enableImmuneLabel);

		logLabel = new JLabel("Enable Logging:");
		logLabel.setSize(300,30);
		logLabel.setLocation(logLabelLocation);
		sirsPanel.add(logLabel);

		tickCountLabel = new JLabel("Update Count: 0",SwingConstants.CENTER);
		tickCountLabel.setSize(300,30);
		tickCountLabel.setLocation(tickCountLabelLocation);
		tickCountLabel.setBorder(BorderFactory.createEtchedBorder());
		sirsPanel.add(tickCountLabel);

		repaintLabel = new JLabel("Repaint on Update:",SwingConstants.CENTER);
		repaintLabel.setSize(300,30);
		repaintLabel.setLocation(repaintLabelLocation);
		sirsPanel.add(repaintLabel);
		
		rectangularCellBoxLabel = new JLabel("Rectangular:",SwingConstants.CENTER);
		rectangularCellBoxLabel.setSize(300,30);
		rectangularCellBoxLabel.setLocation(rectangularCellBoxLabelLocation);
		sirsPanel.add(rectangularCellBoxLabel);
		
		circularCellBoxLabel = new JLabel("Circular:",SwingConstants.CENTER);
		circularCellBoxLabel.setSize(300,30);
		circularCellBoxLabel.setLocation(circularCellBoxLabelLocation);
		sirsPanel.add(circularCellBoxLabel);

	}

	/**
	 * Initialize default location for each UI component.
	 */
	private void initLocations() {
		//bars
		infectionBarLocation = new Point(800,30);
		relapseBarLocation = new Point(800,80);
		cureBarLocation = new Point(800,130);
		tickBarLocation = new Point(800,180);
		sizeBarLocation = new Point(800,560);
		immunityBarLocation = new Point(800,610);

		//color choosers
		infectedColorChooserLocation = new Point(800,400);
		recoveredColorChooserLocation = new Point(980,400);
		susceptibleColorChooserLocation = new Point(1160,400);
		immuneColorChooserLocation = new Point(1340,400);

		//labels
		infectionLabelLocation = new Point (1210,30);
		relapseLabelLocation = new Point(1210,80);
		cureLabelLocation = new Point(1220,130);
		tickLabelLocation = new Point(1190,180);
		sizeLabelLocation = new Point(1200,560);
		infectedColorLabelLocation = new Point(730,404);
		recoveredColorLabelLocation = new Point(895,404);
		susceptibleColorLabelLocation = new Point(1065,404);
		immuneColorLabelLocation = new Point(1270,404);
		immunityLabelLocation = new Point(1180,610);
		enableImmuneLabelLocation = new Point(1000,660);
		logLabelLocation = new Point(1015,335);
		settingsUpdateLabelLocation = new Point(950,305);
		tickCountLabelLocation = new Point(200,60);
		repaintLabelLocation = new Point(170,820);
		circularCellBoxLabelLocation = new Point(65,875);
		rectangularCellBoxLabelLocation = new Point(265,875);
		
		//model
		modelLocation = new Point(50,100);

		//buttons
		toggleButtonLocation = new Point(25,720);
		settingsButtonLocation = new Point(975,250);
		resetButtonLocation = new Point(375,720);
		resetColorsButtonLocation = new Point(975,455);
		updateLatticeButtonLocation = new Point(975,710);

		//checkboxes
		automaticSettingsUpdateBoxLocation = new Point(1180,310);
		immunityCheckBoxLocation = new Point(1130,667);
		logBoxLocation = new Point(1180,340);
		repaintBoxLocation = new Point(400,825);
		circularCellBoxLocation = new Point(280,880);
		rectangularCellBoxLocation = new Point(495,880);

		//population bar
		populationBarLocation = new Point(50,0);
	}

	/**
	 * Initialize buttons in the UI.
	 */
	private void initButtons() {
		toggleButton = new JButton("Start");
		toggleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				toggleState();
			}
		});
		toggleButton.setLocation(toggleButtonLocation);
		toggleButton.setSize(300,100);
		sirsPanel.add(toggleButton);

		settingsButton = new JButton("Update Settings");
		settingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateSettings();
			}
		});
		settingsButton.setLocation(settingsButtonLocation);
		settingsButton.setSize(200,40);
		sirsPanel.add(settingsButton);

		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
			}
		});
		resetButton.setLocation(resetButtonLocation);
		resetButton.setSize(300,100);
		sirsPanel.add(resetButton);

		resetColorsButton = new JButton("Reset Colors");
		resetColorsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetColors();

			}


		});
		resetColorsButton.setLocation(resetColorsButtonLocation);
		resetColorsButton.setSize(200,40);
		sirsPanel.add(resetColorsButton);

		updateLatticeButton = new JButton("Update Lattice");
		updateLatticeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateLatticeModel();
			}


		});
		updateLatticeButton.setLocation(updateLatticeButtonLocation);
		updateLatticeButton.setSize(200,40);
		sirsPanel.add(updateLatticeButton);
	}

	/**
	 * Reset colors for infected, susceptible and cured cells to their default values.
	 */
	private void resetColors() {
		modelComponent.resetColors();
		infectedColor = modelComponent.getInfectedColor();
		recoveredColor = modelComponent.getRecoveredColor();
		susceptibleColor = modelComponent.getSusceptibleColor();
		immuneColor = modelComponent.getImmuneColor();

		infectedColorChooser.setColor(infectedColor);
		recoveredColorChooser.setColor(recoveredColor);
		susceptibleColorChooser.setColor(susceptibleColor);
		immuneColorChooser.setColor(immuneColor);
		repaint();
	}

	/**
	 * Returns the model to its initial macro-state and updates the UI accordinly.
	 */
	protected void reset() {
		//infectionBar.setValue(50);
		//cureBar.setValue(50);
		//relapseBar.setValue(50);
		//tickBar.setValue(100);
		//updatesPerTick = 100;

		running = false;
		toggleButton.setText("Start");
		stopTimer();
		model.reset();
		updatePopulationBar();
		updateTickCountLabel();
		if (enableLogging) {
			closeLogger();
			initLogger();
		}
		repaint();
	}

	/**
	 * Update the model settings based on those given in the UI.
	 */
	protected void updateSettings() {
		if (ready) {
			model.setInfectionProbability(infectionProbability);
			model.setRelapseProbability(relapseProbability);
			model.setRecoveryProbability(recoveryProbability);
			updatesPerTick = tickBar.getValue();
		}
	}

	/**
	 * Toggles the model's running state (i.e. pause if running, and resume if paused)
	 */
	protected void toggleState() {
		running = !running;

		if (running) {
			toggleButton.setText("Stop");
			startTimer();
		} else {
			toggleButton.setText("Start");
			stopTimer();
		}
	}

	/**
	 * Initialize scrollbars in the UI
	 */
	private void initScrollBars() {
		Dimension barSize = new Dimension(300,30);

		infectionBar = new JScrollBar(Scrollbar.HORIZONTAL);
		infectionBar.setMinimum(0);
		infectionBar.setMaximum(110);
		infectionBar.setSize(barSize);
		infectionBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				updateInfectionProbability();
			}
		});
		infectionBar.setLocation(infectionBarLocation);
		infectionBar.setValue(50);
		sirsPanel.add(infectionBar);

		relapseBar = new JScrollBar(Scrollbar.HORIZONTAL);
		relapseBar.setMinimum(0);
		relapseBar.setMaximum(110);
		relapseBar.setSize(barSize);
		relapseBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				updateRelapseProbability();
			}
		});
		relapseBar.setLocation(relapseBarLocation);
		relapseBar.setValue(50);
		sirsPanel.add(relapseBar);

		cureBar = new JScrollBar(Scrollbar.HORIZONTAL);
		cureBar.setMinimum(0);
		cureBar.setMaximum(110);
		cureBar.setSize(barSize);
		cureBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				updateRecoveryProbability();
			}
		});
		cureBar.setLocation(cureBarLocation);
		cureBar.setValue(50);
		sirsPanel.add(cureBar);

		tickBar = new JScrollBar(Scrollbar.HORIZONTAL);
		tickBar.setMinimum(0);
		tickBar.setMaximum(100010);
		tickBar.setSize(barSize);
		tickBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				tickLabel.setText(tickBar.getValue() + " Updates per tick");
				if (automaticSettingsUpdate) {
					updatesPerTick = tickBar.getValue();
				}
			}
		});
		tickBar.setLocation(tickBarLocation);
		tickBar.setValue(updatesPerTick);
		sirsPanel.add(tickBar);

		sizeBar = new JScrollBar(Scrollbar.HORIZONTAL);
		sizeBar.setMinimum(5);
		sizeBar.setMaximum(610);
		sizeBar.setSize(barSize);
		sizeBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				sizeLabel.setText("Lattice Size: " + sizeBar.getValue());
				size = sizeBar.getValue();
			}
		});
		sizeBar.setLocation(sizeBarLocation);
		sizeBar.setValue(width);
		sirsPanel.add(sizeBar);

		immuneProportion = 50; //note: due to int requirement this is a PERCENTAGE value
		immunityBar = new JScrollBar(Scrollbar.HORIZONTAL);
		immunityBar.setMinimum(0);
		immunityBar.setMaximum(110);
		immunityBar.setSize(barSize);
		immunityBar.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				immuneProportion = immunityBar.getValue();
				immunityLabel.setText("Immune Proportion: " + (double)immuneProportion/100);
			}
		});
		immunityBar.setLocation(immunityBarLocation);
		immunityBar.setValue(immuneProportion);
		sirsPanel.add(immunityBar);
	}	



	/**
	 * Update the infection probably based on the settings given in the UI
	 */
	protected void updateInfectionProbability() {
		infectionProbability = (double)infectionBar.getValue() / 100;
		infectionLabel.setText("Infection: " + infectionProbability);
		if (automaticSettingsUpdate) {
			updateSettings();
		}
	}
	/**
	 * Update the relapse probably based on the settings given in the UI
	 */
	protected void updateRelapseProbability() {
		relapseProbability = (double)relapseBar.getValue() / 100;
		relapseLabel.setText("Relapse: " + relapseProbability);
		if (automaticSettingsUpdate) {
			updateSettings();
		}
	}

	/**
	 * Update the recovery probably based on the settings given in the UI
	 */
	protected void updateRecoveryProbability() {
		recoveryProbability = (double)cureBar.getValue() / 100;
		cureLabel.setText("Cure: " + recoveryProbability);
		if (automaticSettingsUpdate) {
			updateSettings();
		}
	}
	
	/**
	 * Resize the window and UI to fit the current screen. Does not work perfectly, but it gets the job done.
	 */
	public void resizeToFitScreen() {
		Dimension original = new Dimension(1920,1080);
		Dimension current = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(current);
		double widthMod = current.getWidth() / original.getWidth();
		double heightMod = current.getHeight() / original.getHeight();
		double scale = Math.min(widthMod,heightMod);
		scale(scale);
	}

	/**
	 * Entry Point
	 * @param args
	 */
	public static void main(String[] args) {
		Simulator gui = new Simulator();
	}


	/**
	 * Update the colors in the model as given in the UI
	 */
	public void updateColors() {
		infectedColor = infectedColorChooser.getColor();
		recoveredColor = recoveredColorChooser.getColor();
		susceptibleColor = susceptibleColorChooser.getColor();
		immuneColor = immuneColorChooser.getColor();
		modelComponent.setInfectedColor(infectedColor);
		modelComponent.setRecoveredColor(recoveredColor);
		modelComponent.setSusceptibleColor(susceptibleColor);
		modelComponent.setImmuneColor(immuneColor);
		repaint();
	}

	public void paint(Graphics g) {
		super.paint(g);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		System.exit(0);
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Update the shape of cells in the model based on the settings given in the UI
	 */
	private void updateCellShape() {
		if (circularCellBox.isSelected()) {
			model.setCellShape(CellShape.CIRCULAR);
		} else if(rectangularCellBox.isSelected()) {
			model.setCellShape(CellShape.RECTANGULAR);
		} else {
			new Error("No shape selected").printStackTrace();
		}
		
		if (repaintOnUpdate) {
			modelComponent.repaint();
		}
	}
}
