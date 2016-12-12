package net.merayen.elastic.ui;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.util.Postmaster;

public abstract class UIObject {
	private UIObject parent;
	final List<UIObject> children = new ArrayList<>(); 

	public net.merayen.elastic.ui.Rect outline_abs_px; // Absolute, in screen pixels

	public int draw_z; // The current *drawn* Z index of this UIObject. Retrieved by the counter from DrawContext()

	public TranslationData translation = new TranslationData();
	public TranslationData absolute_translation;

	protected Draw draw; // Helper class to draw stuff

	private boolean inited = false;
	private boolean alive = true; // TODO Set this to false if this UIObject has been "disposed off". This is to stop any pending async operations
	private boolean attached; // Attached to the tree (reachable from Top) or not

	public final Search search = new Search(this);

	protected void onInit() {} 
	protected void onDraw() {}
	protected void onUpdate() {}
	protected void onEvent(IEvent e) {}

	public void add(UIObject uiobject) {
		add(uiobject, false);
	}

	public void add(UIObject uiobject, boolean first) {
		if(uiobject.parent != null)
			throw new RuntimeException("UIObject already has a parent");

		uiobject.parent = this;
		if(first)
			children.add(0, uiobject);
		else
			children.add(uiobject);

		// Mark every child as attached to the tree
		uiobject.attached = true;
		for(UIObject o : uiobject.search.getAllChildren())
			o.attached = true;
	}

	public void remove(UIObject uiobject) {
		if(!children.contains(uiobject))
			throw new RuntimeException("UIObject is not a child of us");

		if(uiobject.parent != this)
			throw new RuntimeException("Should not happen");

		children.remove(uiobject);
		uiobject.parent = null;

		// Mark every child to be detached
		uiobject.attached = false;
		for(UIObject o : uiobject.search.getAllChildren())
			o.attached = false;
	}

	public UIObject getParent() {
		return parent;
	}

	void initialize() {
		onInit();
		inited = true;
	}

	final void updateDraw(Draw draw) {
		this.draw = draw;

		onDraw();

		this.draw = null;
	}

	public Point getAbsolutePosition(float offset_x, float offset_y) {
		TranslationData td = absolute_translation;
		return new Point((int)(td.x + offset_x / td.scale_x), (int)(td.y + offset_y / td.scale_y)); // Pixel perfect
	}

	public net.merayen.elastic.ui.Point getAbsolutePosition() {
		TranslationData td = absolute_translation;
		return new net.merayen.elastic.ui.Point(td.x, td.y);
	}

	/**
	 * Returns the relative position of the object "obj" to this object.
	 */
	public net.merayen.elastic.ui.Point getRelativePosition(UIObject obj) {
		TranslationData td1 = absolute_translation;
		return new net.merayen.elastic.ui.Point(
			(obj.absolute_translation.x - absolute_translation.x) * absolute_translation.scale_x,
			(obj.absolute_translation.y - absolute_translation.y) * absolute_translation.scale_y
		);
	}

	/**
	 * Get our internal (relative) position from absolute position.
	 */
	public net.merayen.elastic.ui.Point getRelativeFromAbsolute(float x, float y) {
		TranslationData td = absolute_translation;
		return new net.merayen.elastic.ui.Point((x - td.x) * td.scale_x, (y - td.y) * td.scale_y);
	}

	public Dimension getAbsoluteDimension(float width, float height) {
		TranslationData td = absolute_translation;
		return new Dimension((int)(width / td.scale_x), (int)(height / td.scale_y));
	}

	/**
	 * Converts a single unit.
	 * Uses both scale_x and scale_y to figure out the resulting value.
	 */
	public int convertUnitToAbsolute(float a) {
		TranslationData td = absolute_translation;
		float resolution = Math.min(td.scale_x, td.scale_y);
		return (int)(a / resolution);
	}

	/**
	 * Converts a single unit.
	 * Uses both scale_x and scale_y to figure out the resulting value. No it doesn't.
	 */
	public float convertAbsoluteToUnit(int a) { // TODO Only uses the x-scale. Maybe make two functions, one for X and one for Y, and one for both somehow?
		TranslationData td = absolute_translation;

		return (float)a * td.scale_x;
	}

	protected void sendMessage(Postmaster.Message message) {
		UIObject top = search.getTop();
		if(top instanceof Top)
			((Top)top).sendMessage(message);
		else
			System.out.printf("WARNING: Could not send message, UIObject %s is disconnected from Top()\n", this.getClass().getName());
	}

	/**
	 * Evaluates to true if this object has been drawn once or more, so that translation
	 * and other initializing has been done.
	 * You need to check this before calling functions like getPixelDimension etc, as they are
	 * not available until the object has been drawn (and initialized).
	 */
	public boolean isInitialized() {
		return inited;
	}

	public boolean isAlive() {
		return alive;
	}

	public boolean isAttached() {
		return attached;
	}
}
