package net.merayen.elastic.ui.objects.top;

import net.merayen.elastic.ui.Point;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.controller.Gate;
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Postmaster;

/**
 * The very topmost UIObject containing absolutely everything.
 */
public class Top extends UIObject {
	private MouseHandler mousehandler;
	private Gate.UIGate ui_gate;

	// Scrolling, when dragging the background
	float start_scroll_x, start_scroll_y;

	// Cached screen width and height. Updates on every draw. Children UIObjects can use this to get screen size in pixels
	public float width, height;

	public Debug debug;
	private ViewportContainer viewport_container;

	public void setUIGate(Gate.UIGate ui_gate) { // Must be called when inited
		this.ui_gate = ui_gate;
	}

	protected void onInit() {
		viewport_container = new ViewportContainer();
		viewport_container.defaultView();
		add(viewport_container);

		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDrag(Point start_point, Point offset) {
				//node_view.translation.x = (start_scroll_x + offset.x);
				//node_view.translation.y = (start_scroll_y + offset.y);
			}

			@Override
			public void onMouseDown(Point position) {
				//start_scroll_x = node_view.translation.x;
				//start_scroll_y = node_view.translation.y;
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
		/*top_overlay = new TopOverlay();
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
		});*/
	}

	@Override
	protected void onDraw() {
		width = draw.getScreenWidth();
		height = draw.getScreenHeight();

		viewport_container.width = width;
		viewport_container.height = height;

		draw.setColor(0, 0, 0);
		draw.fillRect(0, 0, width, height);

		//debug.set("Top absolute_translation", absolute_translation);
	}

	/*protected void onEvent(IEvent event) {
		mousehandler.handle(event);

		if(event instanceof MouseWheelEvent) {
			MouseWheelEvent e = (MouseWheelEvent)event;

			float s_x = node_view.translation.scale_x;
			float s_y = node_view.translation.scale_y;

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
				Math.max(Math.min(s_x, 10f), 0.1f),
				Math.max(Math.min(s_y, 10f), 0.1f)
			);
		}
	}*/

	/*public ArrayList<UINode> getNodes() {
		return top_node_container.getNodes();
	}*/

	/*public UINode getNode(String id) {
		return top_node_container.getNode(id);
	}*/

	/*public UINet getUINet() {
		return node_view.getUINet();
	}*/

	public float getScreenWidth() {
		return width;
	}

	public float getScreenHeight() {
		return height;
	}

	public ViewportContainer getViewportContainer() { // Note, need to allow multiple viewports when having several windows?
		return viewport_container;
	}

	public void debugPrint(String key, Object value) {
		debug.set(key, value);
	}

	public void sendMessageToBackend(Postmaster.Message message) {
		ui_gate.send(message);
	}
}