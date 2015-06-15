package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.node.Titlebar;
import java.util.ArrayList;

public class Node extends Group {
	
	public float width = 50f;
	public float height = 50f;
	
	private Titlebar titlebar;
	protected ArrayList<Port> ports = new ArrayList<Port>();
	
	protected void onCreate() {
		titlebar = new Titlebar();
		add(titlebar);
	}
	
	@Override
	protected void onDraw(java.awt.Graphics2D g) {
		g.setPaint(new java.awt.Color(50, 50, 50));
		draw.fillRect(0.002f, 0.002f, width, height);
		//g.fill(new java.awt.Rectangle(getAbsolutePixelPoint(0.002f, 0.002f), getPixelDimension(width, height)));
		
		g.setPaint(new java.awt.Color(120, 120, 120));
		draw.fillRect(0, 0, width, height);
		
		
		g.setPaint(new java.awt.Color(180, 180, 180));
		draw.fillRect(0.2f, 0.2f, width - 0.4f, height - 0.4f);
		
		// Testport
		
		titlebar.width = width;
		
		super.onDraw(g);
	}
	
	protected void addPort(Port port) {
		add(port);
		ports.add(port);
	}
}
