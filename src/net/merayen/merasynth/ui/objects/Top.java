package net.merayen.merasynth.ui.objects;

public class Top extends Group {
	protected void onDraw(java.awt.Graphics2D g) {
		
		g.setPaint(new java.awt.Color(80,80,80));
		g.fill(new java.awt.Rectangle(0, 0, draw_context.width, draw_context.height));
		
		super.onDraw(g);
	}
}
