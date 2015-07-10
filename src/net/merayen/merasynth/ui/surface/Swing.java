package net.merayen.merasynth.ui.surface;

import net.merayen.merasynth.ui.DrawContext;
import net.merayen.merasynth.ui.event.DelayEvent;
import net.merayen.merasynth.ui.event.MouseEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;

import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class Swing implements Surface {
	/*
	 * A surface to draw on for the Java Swing GUI. 
	 */	
	public class LolFrame extends javax.swing.JFrame implements java.awt.event.ActionListener {
		public final javax.swing.Timer timer = new javax.swing.Timer(1000/60, this);

		public LolFrame() {
			initUI();
			timer.start();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			repaint();
		}

		private void initUI() {
			addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent we) {
					timer.stop();
				}
			});

			setTitle("MeraSynth");
			setSize(1000, 1000);
			setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			setVisible(true);
		}
	}

	public class LolPanel extends javax.swing.JPanel implements Surface, java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.awt.event.MouseWheelListener {
		public LolPanel() {
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
			this.addMouseWheelListener(this);
		}

		@Override
		public void paintComponent(java.awt.Graphics g) {
			RenderingHints rh = new RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
			((java.awt.Graphics2D)g).setRenderingHints(rh);
			super.paintComponent(g);

			if(handler != null)
				handler.onDraw((java.awt.Graphics2D)g);
		}

		public void mousePressed(java.awt.event.MouseEvent e) {
			createMouseEvent(e, MouseEvent.action_type.DOWN);
		}

		public void mouseReleased(java.awt.event.MouseEvent e) {
			createMouseEvent(e, MouseEvent.action_type.UP);
		}

		public void mouseEntered(java.awt.event.MouseEvent e) {}
		public void mouseExited(java.awt.event.MouseEvent e) {}
		public void mouseClicked(java.awt.event.MouseEvent e) {}

		public void mouseMoved(java.awt.event.MouseEvent e) {
			createMouseEvent(e, MouseEvent.action_type.MOVE);
		}

		public void mouseDragged(java.awt.event.MouseEvent e) {
			createMouseEvent(e, MouseEvent.action_type.MOVE);
		}

		public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
			if(handler != null)
				handler.onMouseWheelEvent(new MouseWheelEvent(e));
		}
	}

	public interface Handler {
		public void onDraw(java.awt.Graphics2D graphics2d);
		public void onMouseEvent(MouseEvent mouse_event);
		public void onMouseWheelEvent(MouseWheelEvent mouse_wheel_event);
	}

	private Handler handler;
	private LolPanel panel;
	private LolFrame frame;
	protected int width = 100;
	protected int height = 100;

	public Swing(Handler handler) {
		this.handler = handler;

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame = new LolFrame();
				panel = new LolPanel();
				frame.add(panel);
			}
		});
	}

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return this.panel.getWidth();
	}

	public int getHeight() {
		return this.panel.getHeight();
	}

	private void createMouseEvent(java.awt.event.MouseEvent e, MouseEvent.action_type ac) {
		if(handler == null)
			return;

		MouseEvent me = new MouseEvent(e, ac);
		handler.onMouseEvent(me);
	}
}
