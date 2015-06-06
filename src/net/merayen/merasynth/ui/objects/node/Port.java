package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.util.MouseHandler;

public class Port extends net.merayen.merasynth.ui.objects.Group {
	/*
	 * Connectable port
	 */
	
	private PortDrag port_drag;
	public String title = "";
	
	protected void onCreate() {
		port_drag = new PortDrag();
		add(port_drag);
	}
	
	protected void onDraw(java.awt.Graphics2D g) {
		g.setPaint(new java.awt.Color(50,200,50));
		draw.fillOval(-0.005f, -0.005f, 0.01f, 0.01f);
		
		g.setColor(new java.awt.Color(0,0,0));
		draw.setFont("SansSerif", 0.015f);
		draw.text(title, 0.01f, 0.005f);
		
		super.onDraw(g);
	}
}
