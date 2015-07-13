package net.merayen.merasynth.ui.objects.top;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;
import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.objects.node.Node;
import net.merayen.merasynth.ui.util.MouseHandler;

public class Top extends Group {
	/*
	 * The topmost object, of 'em all
	 */
	public static abstract class Handler {
		public void onOpenProject(String file_path) {}
		public void onSaveProject() {}
		public void onSaveProjectAs() {}
	}
	private Handler handler;
	private MouseHandler mousehandler;

	// Scrolling, when dragging the background
	float start_scroll_x;
	float start_scroll_y;

	private TopNodeContainer top_node_container = new TopNodeContainer();
	private TopMenuBar top_menu_bar;

	protected void onInit() {
		add(top_node_container);

		top_node_container.translation.scroll_x = -10f;
		top_node_container.translation.scroll_y = -10f;
		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDrag(Point start_point, Point offset) {
				top_node_container.translation.scroll_x = (start_scroll_x - offset.x);
				top_node_container.translation.scroll_y = (start_scroll_y - offset.y);
			}

			@Override
			public void onMouseDown(Point position) {
				start_scroll_x = top_node_container.translation.scroll_x;
				start_scroll_y = top_node_container.translation.scroll_y;
			}
		});

		initMenuBar();
	}
	
	private void initMenuBar() {
		top_menu_bar = new TopMenuBar();
		add(top_menu_bar);
		top_menu_bar.setHandler(new TopMenuBar.Handler() {
			@Override
			public void onOpenProject(String path) {
				if(handler != null)
					handler.onOpenProject(path);
			}

			@Override
			public void onSaveProject() {
				if(handler != null)
					handler.onSaveProject();
			}

			@Override
			public void onSaveProjectAs() {
				if(handler != null)
					handler.onSaveProjectAs();
			}
		});
	}

	protected void onDraw() {
		draw.setColor(50, 50, 50);
		draw.fillRect(-draw_context.width/2, -draw_context.height/2, draw_context.width, draw_context.height); // XXX Ikke bruk draw_context, men meh

		super.onDraw();
	}

	protected void onEvent(IEvent event) {
		mousehandler.handle(event);

		if(event instanceof MouseWheelEvent) {
			MouseWheelEvent e = (MouseWheelEvent)event;

			float p_x = top_node_container.translation.scale_x;
			float p_y = top_node_container.translation.scale_y;

			if(e.getOffsetY() < 0) {
				top_node_container.translation.scale_x /= 1.1;
				top_node_container.translation.scale_y /= 1.1;
			}
			else if(e.getOffsetY() > 0) {
				top_node_container.translation.scale_x *= 1.1;
				top_node_container.translation.scale_y *= 1.1;
			} else {
				return;
			}

			top_node_container.translation.scale_x = Math.min(Math.max(top_node_container.translation.scale_x, .1f), 10f);
			top_node_container.translation.scale_y = Math.min(Math.max(top_node_container.translation.scale_y, .1f), 10f);

			top_node_container.translation.scroll_x -= (top_node_container.translation.scale_x - p_x)*translation.scale_x/2;
			top_node_container.translation.scroll_y -= (top_node_container.translation.scale_y - p_y)*translation.scale_y/2;
		}
	}

	public Node addNode(String class_path) {
		return top_node_container.addNode(class_path);
	}

	public ArrayList<Node> getNodes() {
		return top_node_container.getNodes();
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public JSONObject dump() {
		return top_node_container.dump();
	}

	public void restore(JSONObject obj) {
		top_node_container.restore(obj);
	}
}