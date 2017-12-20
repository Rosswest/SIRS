package sirs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class ModelComponent extends JComponent{
	private SIRSModel model;
	public static final Color DEFAULT_INFECTED_COLOR = Color.red;
	public static final Color DEFAULT_RECOVERED_COLOR = Color.green;
	public static final Color DEFAULT_SUSCEPTIBLE_COLOR = Color.white;
	public static final Color DEFAULT_IMMUNE_COLOR = Color.DARK_GRAY;

	private Color infectedColor;
	private Color recoveredColor;
	private Color susceptibleColor;
	private Color immuneColor;
	
	private CellShape cellShape;
	
	public static final CellShape DEFAULT_CELL_SHAPE = CellShape.CIRCULAR;
	
	public void resetColors() {
		infectedColor = DEFAULT_INFECTED_COLOR;
		recoveredColor = DEFAULT_RECOVERED_COLOR;
		susceptibleColor = DEFAULT_SUSCEPTIBLE_COLOR;
		immuneColor = DEFAULT_IMMUNE_COLOR;
	}
	public static final Dimension DEFAULT_SIZE = new Dimension(800,800);
	
	public void setCellShape(CellShape cellShape) {
		this.cellShape = cellShape;
	}
	
	public CellShape getCellShape() {
		return cellShape;
	}
	
	public ModelComponent(SIRSModel model) {
		this.model = model;
		initialise();
	}

	private void initialise() {
		setSize(DEFAULT_SIZE);
		resetColors();
		cellShape = DEFAULT_CELL_SHAPE;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		int arrayWidth = model.getWidth();
		int arrayHeight = model.getHeight();
		int graphicalWidth = getWidth();
		int graphicalHeight = getHeight();
		int cellWidth = graphicalWidth / arrayWidth;
		int cellHeight = graphicalHeight / arrayHeight;
		int radius = graphicalWidth / arrayWidth;
		cellShape = model.getCellShape(); //possible redundancy with little overhead - kept in just to be safe
                        
		for (int i = 0; i < arrayHeight; i++) {
			int y = i * cellHeight;
			for (int j = 0; j < arrayWidth; j++) {
				int x = j * cellWidth;
				Color cellColor = determineColor(model.getState(j,i));
				g.setColor(cellColor);
				
				if (cellShape == CellShape.CIRCULAR) {
					//circular
					g.fillOval(x,y, radius, radius);
				} else {
					//rectangular
					g.fillRect(x, y, cellWidth, cellHeight);
				}
				
			}
		}

	}
	private Color determineColor(State state) {
		if (state == State.INFECTED) {
			return infectedColor;
		} else if (state == State.SUSCEPTIBLE) {
			return susceptibleColor;
		} else if (state == State.RECOVERED) {
			return recoveredColor;
		} else if (state == State.IMMUNE) {
			return immuneColor;
		}
		return null;
	}

	public SIRSModel getModel() {
		return model;
	}

	public void setModel(SIRSModel model) {
		this.model = model;
	}

	public Color getInfectedColor() {
		return infectedColor;
	}

	public void setInfectedColor(Color infectedColor) {
		this.infectedColor = infectedColor;
	}

	public Color getRecoveredColor() {
		return recoveredColor;
	}

	public void setRecoveredColor(Color recoveredColor) {
		this.recoveredColor = recoveredColor;
	}

	public Color getSusceptibleColor() {
		return susceptibleColor;
	}

	public void setSusceptibleColor(Color susceptibleColor) {
		this.susceptibleColor = susceptibleColor;
	}

	public Color getImmuneColor() {
		return immuneColor;
	}

	public void setImmuneColor(Color immuneColor) {
		this.immuneColor = immuneColor;
	}
}
