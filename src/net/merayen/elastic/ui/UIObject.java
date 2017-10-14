package net.merayen.elastic.ui;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.ui.util.UINodeUtil;
import net.merayen.elastic.util.Point;
import net.merayen.elastic.util.Postmaster;

public class UIObject {
	private UIObject parent;
	final List<UIObject> children = new ArrayList<>(); 

	public net.merayen.elastic.ui.Rect outline_abs_px = new Rect(); // Absolute, in screen pixels
	Rect outline = new Rect();

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

	/**
	 * Override this method to artificially set the children of the UIObject in a onDraw(), onUpdate() and onEvent() call tree.
	 * Should only be used in very specific cases, like by the Top() object to draw different trees for different Window()s.
	 */
	protected List<UIObject> onGetChildren(String surface_id) {
		return this.children;
	}

	public final void add(UIObject uiobject) {
		add(uiobject, children.size());
	}

	public void add(UIObject uiobject, int index) {
		if(uiobject.parent != null)
			throw new RuntimeException("UIObject already has a parent");

		uiobject.parent = this;
		children.add(index, uiobject);

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

	public net.merayen.elastic.util.Point getAbsolutePosition() {
		TranslationData td = absolute_translation;
		return new net.merayen.elastic.util.Point(td.x, td.y);
	}

	/**
	 * Returns the relative position of the object "obj" to this object.
	 */
	public net.merayen.elastic.util.Point getRelativePosition(UIObject obj) {
		return new net.merayen.elastic.util.Point(
			(obj.absolute_translation.x - absolute_translation.x) * absolute_translation.scale_x,
			(obj.absolute_translation.y - absolute_translation.y) * absolute_translation.scale_y
		);
	}

	/**
	 * Get our internal (relative) position from absolute position.
	 */
	public net.merayen.elastic.util.Point getRelativeFromAbsolute(float x, float y) {
		TranslationData td = absolute_translation;
		return new net.merayen.elastic.util.Point((x - td.x) * td.scale_x, (y - td.y) * td.scale_y);
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

	/**
	 * Retrieves a copy of the drawn outline of this object.
	 * This is the outline of the previous paint, so it will
	 * not be available before the first paint.
	 */
	public Rect getOutline() {
		if(outline == null)
			return null;

		return new Rect(outline);
	}

	/**
	 * Retrieves the deep outline of the object (including the children's outline)
	 */
	public Rect getDeepOutline() {
		Rect result = outline_abs_px;

		for(UIObject o : search.getAllChildren()) {
			if(o.outline_abs_px == null) continue;

			if(result == null)
				result = new Rect(o.outline_abs_px);
			else
				result.enlarge(o.outline_abs_px);
		}

		// TODO apply absolute clip

		if(result == null)
			return null;

		result.x1 -= absolute_translation.x;
		result.y1 -= absolute_translation.y;
		result.x2 -= absolute_translation.x;
		result.y2 -= absolute_translation.y;

		result.x1 *= (absolute_translation.scale_x);
		result.y1 *= (absolute_translation.scale_y);
		result.x2 *= (absolute_translation.scale_x);
		result.y2 *= (absolute_translation.scale_y);

		return result;
	}

	protected void sendMessage(Postmaster.Message message) {
		Top top = UINodeUtil.getTop(this);
		if(top != null)
			top.sendMessage(message);
		else
			System.out.printf("WARNING: Could not send message, UIObject %s is disconnected from Top()\n", getClass().getName());
	}

	/**
	 * Retrieves the width of the object.
	 * Feel free to override to return a custom value.
	 */
	public float getWidth() {
		return outline.x2 - outline.x1;
	}

	/**
	 * Retrieves the width of the object.
	 * Feel free to override to return a custom value.
	 */
	public float getHeight() {
		return outline.y2 - outline.y1;
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
