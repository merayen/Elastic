package net.merayen.merasynth.test;

import javax.swing.JFrame;


public class Main extends javax.swing.JFrame {
	
	public class Surface extends net.merayen.merasynth.ui.Surface implements java.awt.event.ActionListener {

		public final javax.swing.Timer timer = new javax.swing.Timer(1000/30, this);
		
		public Surface() {
			timer.start();
		}
	}
	
	public Main() {
		initUI();
	}
	
	private void initUI() {
		final Surface surface = new Surface();
		add(surface);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent we) {
				surface.timer.stop();
			}
		});
		
		setTitle("MeraSynth");
		setSize(1000,1000);
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String sdlkfhs[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				Main m = new Main();
				m.setVisible(true);
			}
		});
	}
}
