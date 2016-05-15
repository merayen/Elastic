package net.merayen.elastic.ui.objects.top;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.controller.Gate;
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer;
import net.merayen.elastic.util.Postmaster;

/**
 * The very topmost UIObject containing absolutely everything.
 */
public class Top extends UIObject {
	private Gate.UIGate ui_gate;

	// Scrolling, when dragging the background
	float start_scroll_x, start_scroll_y;

	// Cached screen width and height. Updates on every draw. Children UIObjects can use this to get screen size in pixels
	public float width, height;

	public Debug debug;
	private ViewportContainer viewport_container;

	public Top() {
		viewport_container = new ViewportContainer();
		add(viewport_container);

		initDebug();
	}

	public void setUIGate(Gate.UIGate ui_gate) { // Must be called when inited
		this.ui_gate = ui_gate;
	}

	private void initDebug() {
		debug = new Debug();
		debug.translation.y = 40f;
		debug.translation.scale_x = .1f;
		debug.translation.scale_y = .1f;
		add(debug);
		debug.set("DEBUG", "Has been enabled");
	}

	@Override
	protected void onDraw() {
		width = draw.getScreenWidth();
		height = draw.getScreenHeight();

		draw.setColor(100, 100, 100);
		draw.fillRect(0, 0, width, height);
	}

	@Override
	protected void onUpdate() {
		viewport_container.width = width;
		viewport_container.height = height - 10;
	}

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

	public void sendMessage(Postmaster.Message message) {
		ui_gate.send(message);
	}
}