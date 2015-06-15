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
			
			float p_x = translation.scale_x;
			float p_y = translation.scale_y;
			
			if(e.getOffsetY() > 0) {
				translation.scale_x /= 1.1;
				translation.scale_y /= 1.1;
			}
			else if(e.getOffsetY() < 0) {
				translation.scale_x *= 1.1;
				translation.scale_y *= 1.1;
			} else {
				return;
			}
			
			translation.scale_x = Math.max(Math.min(translation.scale_x, 10f), .005f);
			translation.scale_y = Math.max(Math.min(translation.scale_y, 10f), .005f);
			
			/*translation.scroll_x -= (translation.scale_x - p_x)*100;
			translation.scroll_y -= (translation.scale_y - p_y)*100;
			
			System.out.printf("Scroll X=%f, scroll Y=%f\n", translation.scroll_x, translation.scroll_y);*/
		}
	}
}
