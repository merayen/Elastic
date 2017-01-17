package net.merayen.elastic.ui.objects.top;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.top.viewport.ViewportContainer;
import net.merayen.elastic.util.Postmaster;

/**
 * The very topmost UIObject containing absolutely everything.
 * Represents a certain UIData in a certain Node-group where it gets all the properties from.
 */
public class Window extends UIObject {
	private final Top top;

	/**
	 * The Node-group we represent. We get all our properties (like dimensions of our windows) from this group.
	 * The group must have a UIData-node that actually contains all these properties. 
	 */
	private String group;

	// Scrolling, when dragging the background
	float start_scroll_x, start_scroll_y;

	// Cached screen width and height. Updates on every draw. Children UIObjects can use this to get screen size in pixels
	public float width, height;

	/**
	 * Windows and other popups can be put here.
	 * UIObjects put here are put on top of everything.
	 */
	public final UIObject overlay = new UIObject();

	public Debug debug;
	private ViewportContainer viewport_container;

	public Window(Top top) {
		this.top = top;
		viewport_container = new ViewportContainer();
		add(viewport_container);

		initDebug();

		add(overlay);
	}

	private void initDebug() {
		debug = new Debug();
		debug.translation.y = 40f;
		debug.translation.scale_x = .1f;
		debug.translation.scale_y = .1f;
		//add(debug);
		debug.set("DEBUG", "Has been enabled");
	}

	@Override
	protected void onDraw() {
		width = draw.getScreenWidth();
		height = draw.getScreenHeight();
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

	/**
	 * Retrieves the group this Top()-object represents.
	 * @return
	 */
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}