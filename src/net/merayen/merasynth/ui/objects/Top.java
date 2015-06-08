package net.merayen.merasynth.ui.objects;

import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;

public class Top extends Group {
	Net net;
	
	protected void onCreate() {
		net = new Net();
		add(net, true); // Add the net topmost
	}
	
	protected void onDraw(java.awt.Graphics2D g) {
		
		g.setPaint(new java.awt.Color(80,80,80));
		g.fill(new java.awt.Rectangle(0, 0, draw_context.width, draw_context.height));
		
		super.onDraw(g);
	}
	
	protected void onEvent(IEvent event) {
		if(event instanceof MouseWheelEvent) {
			MouseWheelEvent e = (MouseWheelEvent)event;
			if(e.getOffsetY() > 0) {
				translation.scale_x /= 1.1;
				translation.scale_y /= 1.1;
			}
			else if(e.getOffsetY() < 0) {
				translation.scale_x *= 1.1;
				translation.scale_y *= 1.1;
			}
			
			if(translation.scale_x < 0.1f)
				translation.scale_x = 0.1f;
			if(translation.scale_y < 0.1f)
				translation.scale_y = 0.1f;
			
			translation.scale_x = Math.max(Math.min(translation.scale_x, 10f), 0.1f);
			translation.scale_y = Math.max(Math.min(translation.scale_y, 10f), 0.1f);
		}
	}
}
