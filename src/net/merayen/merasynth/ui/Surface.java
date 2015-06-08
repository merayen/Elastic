package net.merayen.merasynth.ui;

import net.merayen.merasynth.ui.event.MouseEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;
import java.util.ArrayList;

public abstract class Surface extends javax.swing.JPanel implements java.awt.event.MouseListener, java.awt.event.MouseMotionListener, java.awt.event.MouseWheelListener {

	protected int width = 100;
	protected int height = 100;
	
	ArrayList<net.merayen.merasynth.ui.event.IEvent> events_queue = new ArrayList<net.merayen.merasynth.ui.event.IEvent>();
	
	net.merayen.merasynth.ui.objects.Group top_ui_object = new net.merayen.merasynth.ui.objects.Top(); // Topmost object containing everything
	
	public Surface() {
		for(int i = 0; i < 1; i++) {
		net.merayen.merasynth.ui.objects.node.Node node = new net.merayen.merasynth.ui.objects.client.PulseGenerator();
		node.translation.x = 0.1f + i/100f;
		node.translation.y = 0.1f + i/100f;
		node.width = 0.1f;
		node.height = 0.1f;
		top_ui_object.add(node);
		top_ui_object.translation.scale_x = 1.0f;
		top_ui_object.translation.scale_y = 1.0f;
		}
		
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
	}
	
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void paintComponent(java.awt.Graphics g) {
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
	
	public void mouseClicked(java.awt.event.MouseEvent e) {
		//events_queue.add(new MouseEvent(e, MouseEvent.action_type.CLICKED));
	}
	
	public void mousePressed(java.awt.event.MouseEvent e) {
		events_queue.add(new MouseEvent(e, MouseEvent.action_type.DOWN));
	}
	
    public void mouseReleased(java.awt.event.MouseEvent e) {
    	events_queue.add(new MouseEvent(e, MouseEvent.action_type.UP));
    }
    
    public void mouseEntered(java.awt.event.MouseEvent e) {
    	//events_queue.add(new MouseEvent(e, MouseEvent.action_type.OVER));
    }
    
    public void mouseExited(java.awt.event.MouseEvent e) {
    	//events_queue.add(new MouseEvent(e, MouseEvent.action_type.OUT));
    }
    
    public void mouseMoved(java.awt.event.MouseEvent e) {
    	events_queue.add(new MouseEvent(e, MouseEvent.action_type.MOVE));
    }
    
    public void mouseDragged(java.awt.event.MouseEvent e) {
    	events_queue.add(new MouseEvent(e, MouseEvent.action_type.MOVE));
    }
    
    @Override
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
    	events_queue.add(new MouseWheelEvent(e));
    }
}
