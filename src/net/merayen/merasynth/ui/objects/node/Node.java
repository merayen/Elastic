package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.node.Titlebar;
import java.util.ArrayList;

public class Node extends Group {
	
	public float width = 0.5f;
	public float height = 0.5f;
	
	private Titlebar titlebar;
	private ArrayList<Port> ports = new ArrayList<Port>();
	
	protected void onCreate() {
		titlebar = new Titlebar();
		add(titlebar);
		
		Port port = new Port();
		add(port);
		ports.add(new Port());
	}
	
	@Override
	protected void onDraw(java.awt.Graphics2D g) {
		g.setPaint(new java.awt.Color(50, 50, 50));
		draw.fillRect(0.002f, 0.002f, width, height);
		//g.fill(new java.awt.Rectangle(getAbsolutePixelPoint(0.002f, 0.002f), getPixelDimension(width, height)));
		
		g.setPaint(new java.awt.Color(120, 120, 120));
		draw.fillRect(0, 0, width, height);
		//g.fill(new java.awt.Rectangle(getAbsolutePixelPoint(0, 0), getPixelDimension(width, height)));
		
		
		g.setPaint(new java.awt.Color(180, 180, 180));
		draw.fillRect(0.002f, 0.002f, width - 0.004f, height - 0.004f);
		//g.fill(new java.awt.Rectangle(getAbsolutePixelPoint(0.002f, 0.002f), getPixelDimension(width - 0.004f, height - 0.004f)));
		
		// Testport
		
		titlebar.width = width;
		
		super.onDraw(g);
	}
}
