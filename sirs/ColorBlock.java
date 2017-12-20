package sirs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JColorChooser;

public class ColorBlock extends Component implements MouseListener {
	private Color color;
	private Simulator gui;
	
	public ColorBlock(Color color, Simulator gui) {
		this.color = color;
		this.gui = gui;
		addMouseListener(this);
	}

	public void paint(Graphics g) {
		super.paint(g);
		int width = getWidth();
		int height = getHeight();
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		
		g.setColor(Color.black);
		
		g.drawLine(0, 0, 0, height);
		g.drawLine(0,0,width,0);
		g.drawLine(width,0,width,height);
		g.drawLine(0,height,width,height);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		Color nextColor = JColorChooser.showDialog(null, "Choose a Color", color);
		if (nextColor != null) {
			color = nextColor;
			gui.updateColors();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		repaint();
	}

	public Simulator getGui() {
		return gui;
	}
	
	

}
