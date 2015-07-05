package net.merayen.merasynth.ui;

import net.merayen.merasynth.ui.event.DelayEvent;
import net.merayen.merasynth.ui.event.MouseEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;

import java.awt.RenderingHints;
import java.util.ArrayList;

public abstract class Surface extends javax.swing.JPanel implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.awt.event.MouseWheelListener {

	protected int width = 100;
	protected int height = 100;

	ArrayList<net.merayen.merasynth.ui.event.IEvent> events_queue = new ArrayList<net.merayen.merasynth.ui.event.IEvent>();

	net.merayen.merasynth.ui.objects.top.Top top_ui_object = new net.merayen.merasynth.ui.objects.top.Top(); // Topmost object containing everything

	public Surface() {
		for(int i = 0; i < 5; i++) {
			net.merayen.merasynth.ui.objects.node.Node node = new net.merayen.merasynth.ui.objects.client.PulseGenerator();
			node.translation.x = 0f + i;
			node.translation.y = 0f + i;
			node.width = 10f;
			node.height = 10f;
			top_ui_object.addNode(node);
		}

		top_ui_object.translation.scale_x = 100f;
		top_ui_object.translation.scale_y = 100f;

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
	}

	protected void draw(java.awt.Graphics2D g) {
		ArrayList<net.merayen.merasynth.ui.event.IEvent> current_events;

		synchronized (events_queue) {
			current_events = new ArrayList<net.merayen.merasynth.ui.event.IEvent>(events_queue);
			events_queue.clear();
		}

		net.merayen.merasynth.ui.DrawContext dc = new net.merayen.merasynth.ui.DrawContext(g, current_events, getWidth(), getHeight());

		top_ui_object.update(dc);

		executeDelayEvents(dc.outgoing_events);

		// TODO send out the outgoing events?
	}

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void paintComponent(java.awt.Graphics g) {
		RenderingHints rh = new RenderingHints(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		((java.awt.Graphics2D)g).setRenderingHints(rh);
		super.paintComponent(g);
		draw((java.awt.Graphics2D)g);
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		repaint();
	}

	public int getX(float x) {
		return (int)(x*this.width);
	}

	public int getY(float y) {
		return (int)(y*this.height);
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

	@Override
	public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
		events_queue.add(new MouseWheelEvent(e)); // Do collision testing and stuff here too, like MouseEvent!
	}

	private void createMouseEvent(java.awt.event.MouseEvent e, MouseEvent.action_type ac) {
		MouseEvent me = new MouseEvent(e, ac);
		me.calcHit(top_ui_object); // Calculates what UIObject got hit
		events_queue.add(me);
	}

	private void executeDelayEvents(ArrayList<net.merayen.merasynth.ui.event.IEvent>events) {
		for(net.merayen.merasynth.ui.event.IEvent e : events)
			if(e instanceof DelayEvent)
				((DelayEvent) e).run();
	}
}
