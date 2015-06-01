package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.util.MouseHandler;

public class Port extends net.merayen.merasynth.ui.objects.Group {
	/*
	 * Connectable port
	 */
	
	private PortDrag port_drag;
	
	protected void onCreate() {
		port_drag = new PortDrag();
		add(port_drag);
	}
	
	protected void onDraw(java.awt.Graphics2D g) {
		g.setPaint(new java.awt.Color(50,200,50));
		java.awt.Point point = getAbsolutePixelPoint(-0.01f, -0.01f);
		java.awt.Dimension dimension = getPixelDimension(0.01f, 0.01f);
		draw.fillOval(-0.005f, -0.005f, 0.01f, 0.01f);
		
		super.onDraw(g);
		
		// Test
		
		
		/*g.setPaint(new java.awt.Color(0,0,0));
		draw.setStroke(0.002f);
		draw.line(-0.005f, -0.005f, 0.005f, 0.005f);*/
	}
}
