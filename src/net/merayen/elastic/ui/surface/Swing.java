package net.merayen.elastic.ui.surface;

import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.event.KeyboardEvent;
import net.merayen.elastic.ui.event.MouseEvent;
import net.merayen.elastic.ui.event.MouseWheelEvent;

/**
 * Java Swing Surface.
 * TODO move somewhere else?
 */
public class Swing extends Surface {
	/*
	 * A surface to draw on for the Java Swing GUI. 
	 */	
	public class LolFrame extends javax.swing.JFrame implements java.awt.event.ActionListener {
		private Runnable close_function;
		public final javax.swing.Timer timer = new javax.swing.Timer(1000/60, this);

		public LolFrame(Runnable close_function) {
			this.close_function = close_function;
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
					close_function.run();
				}
			});

			setTitle("Elastic");
			setSize(1000, 1000);
			setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			setVisible(true);
		}

		public void end() {
			timer.stop();
		}
	}

	public class LolPanel extends javax.swing.JPanel implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.awt.event.MouseWheelListener, java.awt.event.KeyListener {
		private Set<Integer> active_key_codes = new HashSet<>(); // Ugly hack as JKeyListener repeats the keys, at least for Linux

		public LolPanel() {
			setFocusable(true);
			//grabFocus();
			addMouseListener(this);
			addMouseMotionListener(this);
			addMouseWheelListener(this);
			addKeyListener(this);
		}

		@Override
		public void paintComponent(java.awt.Graphics g) {
			//RenderingHints rh = new RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
			//((java.awt.Graphics2D)g).setRenderingHints(rh);
			super.paintComponent(g);

			handler.onDraw((java.awt.Graphics2D)g);
		}

		public void mousePressed(java.awt.event.MouseEvent e) {
			createMouseEvent(e, MouseEvent.Action.DOWN);
		}

		public void mouseReleased(java.awt.event.MouseEvent e) {
			createMouseEvent(e, MouseEvent.Action.UP);
		}

		public void mouseEntered(java.awt.event.MouseEvent e) {}
		public void mouseExited(java.awt.event.MouseEvent e) {}
		public void mouseClicked(java.awt.event.MouseEvent e) {}

		public void mouseMoved(java.awt.event.MouseEvent e) {
			createMouseEvent(e, MouseEvent.Action.MOVE);
		}

		public void mouseDragged(java.awt.event.MouseEvent e) {
			createMouseEvent(e, MouseEvent.Action.MOVE);
		}

		public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
			handler.onEvent(new MouseWheelEvent(e));
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void keyPressed(KeyEvent e) {
			if(!active_key_codes.contains(e.getKeyCode())) {
				active_key_codes.add(e.getKeyCode());
				handler.onEvent(new KeyboardEvent(e.getKeyChar(), e.getKeyCode(), KeyboardEvent.Action.DOWN));
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(active_key_codes.contains(e.getKeyCode())) {
				active_key_codes.remove(e.getKeyCode());
				handler.onEvent(new KeyboardEvent(e.getKeyChar(), e.getKeyCode(), KeyboardEvent.Action.UP));
			}
		}
	}

	private LolPanel panel;
	private LolFrame frame;

	public Swing(String id, Handler handler) {
		super(id, handler);

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				frame = new LolFrame(new Runnable() {
					@Override
					public void run() {
						end();
					}
				});
				panel = new LolPanel();
				frame.add(panel);
			}
		});
	}

	public int getWidth() {
		return this.panel.getWidth();
	}

	public int getHeight() {
		return this.panel.getHeight();
	}

	private void createMouseEvent(java.awt.event.MouseEvent e, MouseEvent.Action action) {
		MouseEvent.Button button = null;

		int b = e.getButton();

		if(b == java.awt.event.MouseEvent.BUTTON1)
			button = MouseEvent.Button.LEFT;
		else if(b == java.awt.event.MouseEvent.BUTTON2)
			button = MouseEvent.Button.MIDDLE;
		else if(b == java.awt.event.MouseEvent.BUTTON3)
			button = MouseEvent.Button.RIGHT;
			
		handler.onEvent(new MouseEvent(e.getX(), e.getY(), action, button));
	}

	@Override
	public void end() {
		frame.setVisible(false);
		frame.dispose();
		frame.timer.stop();
		
		frame = null;
		panel = null;
	}
}
