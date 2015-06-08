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
		public Port a, b;
		
		public Connection(Port a, Port b) {
			this.a = a;
			this.b = b;
		}
	}
	
	ArrayList<Connection> connections = new ArrayList<Connection>(); 
	
	protected void onDraw(java.awt.Graphics2D g) {
		g.setColor(new Color(0,0,0));
		
		for(Connection c : connections) {
			Point p1 = c.a.getAbsolutePosition();
			Point p2 = c.b.getAbsolutePosition();
			System.out.printf("Linje %s, <--> %s", p1, p2);
			draw.line(p1.x, p1.y, p2.x, p2.y);
		}
		
		super.onDraw(g);
	}
	
	public void addLine(Port a, Port b) {
		for(Connection c : connections)
			if((c.a == a && c.b == b) || (c.a == b && c.b == a))
				throw new RuntimeException("Port is already connected.");
		
		connections.add(new Connection(a, b));
	}
	
	public void removeLine(Port a, Port b) {
		for(int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			if((c.a == a && c.b == b) || (c.a == b && c.b == a)) {
				connections.remove(i);
				return;
			}
		}
	}
}
