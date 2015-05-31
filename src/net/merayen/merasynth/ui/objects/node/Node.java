package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.node.Titlebar;

public class Node extends Group {
	
	public float width = 0.5f;
	public float height = 0.5f;
	
	private Titlebar titlebar;
	
	protected void onCreate() {
		titlebar = new Titlebar();
		this.add(titlebar);
	}
	
	@Override
	protected void onDraw(java.awt.Graphics2D g) {
		g.setPaint(new java.awt.Color(50, 50, 50));
		g.fill(new java.awt.Rectangle(getAbsolutePixelPoint(0.002f, 0.002f), getPixelDimension(width, height)));
		
		g.setPaint(new java.awt.Color(120, 120, 120));
		g.fill(new java.awt.Rectangle(getAbsolutePixelPoint(0, 0), getPixelDimension(width, height)));
		
		g.setPaint(new java.awt.Color(180, 180, 180));
		g.fill(new java.awt.Rectangle(getAbsolutePixelPoint(0.002f, 0.002f), getPixelDimension(width - 0.004f, height - 0.004f)));
		
		titlebar.width = width;
		
		super.onDraw(g);
	}
}
