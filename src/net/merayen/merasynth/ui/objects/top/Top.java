package net.merayen.merasynth.ui.objects.top;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import net.merayen.merasynth.glue.nodes.GlueNode;
import net.merayen.merasynth.glue.nodes.GlueTop;
import net.merayen.merasynth.netlist.Supervisor;
import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.event.MouseWheelEvent;
import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.util.MouseHandler;

public class Top extends UIGroup {
	/*
	 * The topmost object, of 'em all
	 */
	public static abstract class Handler {
		public void onOpenProject(String file_path) {}
		public void onSaveProject() {}
		public void onSaveProjectAs() {}
		public void onClose() {}
	}
	private Handler handler;
	private MouseHandler mousehandler;
	private net.merayen.merasynth.glue.Context glue_context;

	// Scrolling, when dragging the background
	float start_scroll_x, start_scroll_y;

	// Cached screen width and height. Updates on every draw
	float screen_width, screen_height;

	double i = Math.PI * 1.5;

	public Debug debug;
	private TopNodeContainer top_node_container = new TopNodeContainer();
	private TopOverlay top_overlay;

	public Top(net.merayen.merasynth.glue.Context glue_context) {
		this.glue_context = glue_context;
	}

	protected void onInit() {
		add(top_node_container);

		// Pixel mapping. Maybe make this setup-able ala DPI somwhere TODO
		translation.scale_x = 1;
		translation.scale_y = 1;

		top_node_container.translation.x = 0f;
		top_node_container.translation.y = 0f;
		top_node_container.translation.scale_x = 1f;
		top_node_container.translation.scale_y = 1f;

		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDrag(Point start_point, Point offset) {
				top_node_container.translation.x = (start_scroll_x + offset.x);
				top_node_container.translation.y = (start_scroll_y + offset.y);
			}

			@Override
			public void onMouseDown(Point position) {
				start_scroll_x = top_node_container.translation.x;
				start_scroll_y = top_node_container.translation.y;
			}
		});

		initDebug();
		initOverlay();
	}

	private void initDebug() {
		debug = new Debug();
		debug.translation.y = 40f;
		debug.translation.scale_x = .1f;
		debug.translation.scale_y = .1f;
		add(debug);
		debug.set("DEBUG", "Has been enabled");
	}

	private void initOverlay() {
		top_overlay = new TopOverlay();
		top_overlay.translation.scale_x = 1f;
		top_overlay.translation.scale_y = 1f;
		add(top_overlay);

		top_overlay.setHandler(new TopOverlay.Handler() {
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

			@Override
			public void onClose() {
				if(handler != null)
					handler.onClose();
			}
		});
	}

	protected void onDraw() {
		screen_width = this.draw_context.width;
		screen_height = this.draw_context.height;
		this.top_overlay.width = screen_width;
		this.top_overlay.height = screen_height;

		draw.setColor(50, 50, 50);
		draw.fillRect(0, 0, screen_width, screen_height);

		debug.set("Top absolute_translation", absolute_translation);

		super.onDraw();
	}

	protected void onEvent(IEvent event) {
		mousehandler.handle(event);

		if(event instanceof MouseWheelEvent) {
			MouseWheelEvent e = (MouseWheelEvent)event;

			float s_x = top_node_container.translation.scale_x;
			float s_y = top_node_container.translation.scale_y;

			if(e.getOffsetY() < 0) {
				s_x /= 1.1f;
				s_y /= 1.1f;
			}
			else if(e.getOffsetY() > 0) {
				s_x *= 1.1f;
				s_y *= 1.1f;
			} else {
				return;
			}
			zoom(
				s_x = Math.max(Math.min(s_x, 10f), 0.1f),
				s_x = Math.max(Math.min(s_x, 10f), 0.1f)
			);
		}
	}

	public UINode addNode(String class_path) {
		return top_node_container.addNode(class_path);
	}

	public ArrayList<UINode> getNodes() {
		return top_node_container.getNodes();
	}

	public UINode getNode(String id) {
		return top_node_container.getNode(id);
	}

	public GlueNode getGlueNode(UINode uinode) {
		/*
		 * Get the GlueNode that represents the uinode.
		 */
		return glue_context.glue_top.getNode(uinode.getID());
	}

	public Supervisor getSupervisor() {
		return glue_context.net_supervisor;
	}

	public GlueTop getGlueTop() {
		return glue_context.glue_top;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public float getScreenWidth() {
		return screen_width;
	}

	public float getScreenHeight() {
		return screen_height;
	}

	public JSONObject dump() {
		return top_node_container.dump();
	}

	public void restore(JSONObject obj) {
		top_node_container.restore(obj);
	}

	private void zoom(float new_scale_x, float new_scale_y) {
		UIGroup tnc = top_node_container;
		float previous_scale_x = tnc.translation.scale_x;
		float previous_scale_y = tnc.translation.scale_y;
		float scale_diff_x = new_scale_x - previous_scale_x;
		float scale_diff_y = new_scale_y - previous_scale_y;
		float current_offset_x = (tnc.translation.x - screen_width  / 2);
		float current_offset_y = (tnc.translation.y - screen_height / 2);

		tnc.translation.scale_x = new_scale_x;
		tnc.translation.scale_y = new_scale_y;
		tnc.translation.x = screen_width  / 2 + current_offset_x + current_offset_x * (-scale_diff_x / new_scale_x);
		tnc.translation.y = screen_height / 2 + current_offset_y + current_offset_y * (-scale_diff_y / new_scale_y);
	}
}