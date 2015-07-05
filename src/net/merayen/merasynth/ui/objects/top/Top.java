package net.merayen.merasynth.ui.objects.top;

import java.awt.Color;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;
import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.Net;
import net.merayen.merasynth.ui.objects.node.Node;
import net.merayen.merasynth.ui.util.MouseHandler;

public class Top extends Group {
	private MouseHandler mousehandler;

	// Scrolling, when dragging the background
	float start_scroll_x;
	float start_scroll_y;

	private TopNodeContainer top_node_container = new TopNodeContainer();

	protected void onInit() {
		// TODO Add basic nodes as a start

		add(top_node_container);

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
			public void onMouseDrop(Point start_point, Point offset) {}

			@Override
			public void onMouseDrag(Point start_point, Point offset) {
				// TODO Auto-generated method stub
				top_node_container.translation.scroll_x = (start_scroll_x - offset.x);
				top_node_container.translation.scroll_y = (start_scroll_y - offset.y);
				System.out.printf("Top scroll: %s, %s\n", start_point, offset);
			}

			@Override
			public void onMouseDown(Point position) {
				start_scroll_x = top_node_container.translation.scroll_x;
				start_scroll_y = top_node_container.translation.scroll_y;
			}

			@Override
			public void onGlobalMouseMove(Point global_position) {}
		});
	}
	
	protected void onDraw(java.awt.Graphics2D g) {
		
		g.setPaint(new Color(50,50,50));
		draw.fillRect(-draw_context.width/2, -draw_context.height/2, draw_context.width, draw_context.height); // XXX Ikke bruk draw_context, men meh
		
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
	
	public void addNode(Node node) {
		top_node_container.add(node);
	}
}
