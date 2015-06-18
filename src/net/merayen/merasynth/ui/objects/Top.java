package net.merayen.merasynth.ui.objects;

import java.awt.Color;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;
import net.merayen.merasynth.ui.util.MouseHandler;

public class Top extends Group {
	private Net net;
	private MouseHandler mousehandler;
	
	protected void onInit() {
		net = new Net();
		add(net, true); // Add the net topmost
		
		// TODO Add basic nodes as a start
		
		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.IMouseHandler() {
			
			@Override
			public void onMouseUp(Point position) {}
			
			@Override
			public void onMouseOver() {}
			
			@Override
			public void onMouseOut() {}
			
			@Override
			public void onMouseMove(Point position) {}
			
			@Override
			public void onMouseDrop(Point start_point, Point offset) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMouseDrag(Point start_point, Point offset) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMouseDown(Point position) {
				System.out.println("Top touched");
			}
			
			@Override
			public void onGlobalMouseMove(Point global_position) {}
		});
	}
	
	protected void onDraw(java.awt.Graphics2D g) {
		
		g.setPaint(new Color(50,50,50));
		draw.fillRect(0, 0, draw_context.width, draw_context.height);
		
		super.onDraw(g);
	}
	
	protected void onEvent(IEvent event) {
		mousehandler.handle(event);
		
		if(event instanceof MouseWheelEvent) {
			MouseWheelEvent e = (MouseWheelEvent)event;
			
			float p_x = translation.scale_x;
			float p_y = translation.scale_y;
			
			if(e.getOffsetY() < 0) {
				translation.scale_x /= 1.1;
				translation.scale_y /= 1.1;
			}
			else if(e.getOffsetY() > 0) {
				translation.scale_x *= 1.1;
				translation.scale_y *= 1.1;
			} else {
				return;
			}
			
			translation.scale_x = Math.min(Math.max(translation.scale_x, 10f), 500f);
			translation.scale_y = Math.min(Math.max(translation.scale_y, 10f), 500f);

			translation.scroll_x -= (translation.scale_x - p_x)/2;
			translation.scroll_y -= (translation.scale_y - p_y)/2;
		}
	}
}
