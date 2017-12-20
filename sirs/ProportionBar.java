package sirs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ProportionBar extends Component{
	private double sum;
	private double[] values;
	private double[] proportions;
	private Color separatorColor = Color.black;
	private Color[] colors = {Color.RED,Color.green,Color.white,Color.DARK_GRAY};

	public Color[] getColors() {
		return colors;
	}

	public void setColors(Color[] colors) {
		this.colors = colors;
	}

	public ProportionBar(double[] values) {
		this.values = values;
		updateProportions();
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
		updateProportions();
	}

	private void updateProportions() {
		proportions = new double[values.length];
		sum = 0;

		for (double val : values) {
			sum += val;
		}

		for (int i = 0; i < values.length; i++) {
			proportions[i] = values[i] / sum;
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		int width = getWidth();
		int height = getHeight();
		int xOffset = 0;

		for (int i = 0; i < proportions.length; i++) {
			g.setColor(colors[i]);
			int boxWidth = (int) (proportions[i] * width);
			g.fillRect(xOffset, 0, boxWidth, height);
			if (i == proportions.length-1) {
				//fill final block
				if (boxWidth == 0) {
					g.setColor(colors[proportions.length-2]);
				}
				int remainingWidth = width - xOffset;
				g.fillRect(xOffset + boxWidth, 0, remainingWidth, height);
			} else {
				//draw separator line
				g.setColor(separatorColor);
				g.drawLine(xOffset,0,xOffset,height);
			}
			xOffset+= boxWidth;

		}
		g.setColor(Color.black);
		g.drawLine(0,0,width-1,0);
		g.drawLine(0, 0, 0, height-1);
		g.drawLine(0, height-1, width-1, height-1);
		g.drawLine(width-1, 0, width-1, height-1);
	}


	public static void main(String[] args) {

	}

}
