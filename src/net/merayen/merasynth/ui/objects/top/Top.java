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
	private TopMenuBar top_menu_bar;

	public Top(net.merayen.merasynth.glue.Context glue_context) {
		this.glue_context = glue_context;
	}

	protected void onInit() {
		add(top_node_container);

		// Pixel mapping. Maybe make this setup-able ala DPI somwhere TODO
		translation.scale_x = 1;
		translation.scale_y = 1;

		top_node_container.translation.x = 400f;
		top_node_container.translation.y = 450f;
		top_node_container.translation.scale_x = .1f;
		top_node_container.translation.scale_y = .1f;

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
		initMenuBar();
	}

	private void initDebug() {
		debug = new Debug();
		debug.translation.y = 40f;
		debug.translation.scale_x = .1f;
		debug.translation.scale_y = .1f;
		add(debug);
		debug.set("DEBUG", "Has been enabled");
	}

	private void initMenuBar() {
		top_menu_bar = new TopMenuBar();
		top_menu_bar.translation.scale_x = .1f;
		top_menu_bar.translation.scale_y = .1f;
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
		draw.setColor(50, 50, 50);
		draw.fillRect(0, 0, screen_width, screen_height);

		draw.setColor(255, 255, 0);
		draw.setStroke(1);
		draw.line(0, screen_height / 2, screen_width, screen_height / 2);
		draw.line(screen_width / 2, 0, screen_width / 2, screen_height);

		/*UIGroup m = top_node_container;
		float previous_scale = m.translation.scale_x;
		float scale = (float)Math.sin(i+=0.05) / 20f + 0.15f;
		float scale_diff = scale - previous_scale;

		float current_offset_x = (m.translation.x - screen_width / 2);
		float current_offset_y = (m.translation.y - screen_height / 2);
		float new_x = screen_width / 2 + current_offset_x + current_offset_x * (-scale_diff / scale);
		float new_y = screen_height / 2 + current_offset_y + current_offset_y * (-scale_diff / scale);

		m.translation.scale_x = scale;
		m.translation.scale_y = scale;
		m.translation.x = new_x;
		m.translation.y = new_y;
		//m.translation.x = (screen_width / 2) + offset_x / scale;
		//m.translation.x = (screen_width / 2) + offset_x / scale;

		debug.set("Z scale_diff", String.format("%3f", scale_diff));
		debug.set("Z current_offset X", String.format("%3f", current_offset_x));
		debug.set("Z new X", String.format("%3f", new_x));*/

		/*if((i = (i+1) % 100) < 50) {
			m.translation.scale_x = 0.1f;
			m.translation.scale_y = 0.1f;
			m.translation.x = 400;
		} else {
			m.translation.scale_x = 0.2f;
			m.translation.scale_y = 0.2f;
			m.translation.x = 450;
		}*/

		debug.set("Top absolute_translation", absolute_translation);

		super.onDraw();
	}

	protected void onEvent(IEvent event) {
		mousehandler.handle(event);

		if(event instanceof MouseWheelEvent) {
			MouseWheelEvent e = (MouseWheelEvent)event;

			float p_x = top_node_container.translation.scale_x;
			float p_y = top_node_container.translation.scale_y;

			if(e.getOffsetY() < 0) {
				zoom(
					top_node_container.translation.scale_x / 1.1f,
					top_node_container.translation.scale_y / 1.1f
				);
			}
			else if(e.getOffsetY() > 0) {
				zoom(
					top_node_container.translation.scale_x * 1.1f,
					top_node_container.translation.scale_y * 1.1f
				);
			} else {
				return;
			}
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