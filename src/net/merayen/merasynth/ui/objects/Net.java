package net.merayen.merasynth.ui.objects;

import java.awt.Color;
import java.util.ArrayList;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.objects.node.Port;

public class Net extends net.merayen.merasynth.ui.objects.Group {
	/* 
	 * Draws the net behind all of the nodes. Must be drawn first.
	 */
	private class Connection {
		public UIObject a, b;
		
		public Connection(UIObject a, UIObject b) {
			this.a = a;
			this.b = b;
		}
	}
	
	ArrayList<Connection> connections = new ArrayList<Connection>();
	
	private UIObject dragging_port;
	
	protected void onDraw(java.awt.Graphics2D g) {
		for(Connection c : connections) {
			Point p1 = c.a.getAbsolutePosition();
			Point p2 = c.b.getAbsolutePosition();
			
			g.setColor(new Color(150,150,150));
			draw.setStroke(0.5f);
			draw.line(p1.x, p1.y, p2.x, p2.y);
			draw.setStroke(0.3f);
			g.setColor(new Color(0,0,0));
			draw.line(p1.x, p1.y, p2.x, p2.y);
		}
		
		super.onDraw(g);
	}
	
	public void addLine(UIObject a, UIObject b) {
		for(Connection c : connections)
			if((c.a == a && c.b == b) || (c.a == b && c.b == a))
				throw new RuntimeException("Port is already connected.");
		
		connections.add(new Connection(a, b));
	}
	
	public void removeLine(UIObject a, UIObject b) {
		for(int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			if((c.a == a && c.b == b) || (c.a == b && c.b == a)) {
				connections.remove(i);
				return;
			}
		}
	}
	
	public void setDraggingPort(UIObject port) {
		/*
		 * Call this when a port is dragging a line from it.
		 * This port can then be retrieved by a hovering port by calling getOtherPort()
		 */
		assert dragging_port == null : "Already dragging";
		dragging_port = port;
	}
	
	public UIObject getDraggingPort() {
		return dragging_port;
	}
}
