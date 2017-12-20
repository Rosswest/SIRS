package sirs;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main {

	public static void main(String[] args) {
		final SIRSModel m = new SIRSModel(100,100);
		for (int i = 0; i < 100; i++) {
			m.infectRandomCell();
		}
		ModelComponent c = new ModelComponent(m);
		//System.out.println(c.getModel());
		final JFrame frame = new JFrame();
		frame.setSize(1200,1200);

		JPanel panel =new JPanel();
		panel.setSize(800,600);
		panel.add(c);
		c.setLocation(25,25);
		//c.setVisible(true);
		//c.setSize(800,600);

		panel.setLayout(null);
		frame.setContentPane(panel);
		frame.setVisible(true);
		frame.repaint();

		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				for (int i = 0; i < 1000; i++) {
					m.updateSequential();
				}
				frame.repaint();
			}

		},16,16);

	}
	
	

}
