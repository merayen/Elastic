package net.merayen.elastic.ui;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.event.IEvent;

public abstract class UIObject {
	private UIObject parent;
	final List<UIObject> children = new ArrayList<>(); 

	public net.merayen.elastic.ui.Rect outline_abs_px; // Absolute, in screen pixels

	public int draw_z; // The current *drawn* Z index of this UIObject. Retrieved by the counter from DrawContext()

	public TranslationData translation = new TranslationData();
	public TranslationData absolute_translation;

	protected Draw draw; // Helper class to draw stuff

	private boolean created = false;
	private boolean alive = true; // TODO Set this to false if this UIObject has been "disposed off". This is to stop any pending async operations
	private final ArrayList<Runnable> when_ready_funcs = new ArrayList<Runnable>();

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
	}

	public void remove(UIObject uiobject) {
		if(!children.contains(uiobject))
			throw new RuntimeException("UIObject is not a child of us");

		if(uiobject.parent != this)
			throw new RuntimeException("Should not happen");

		children.remove(uiobject);
		uiobject.parent = null;
	}

	public UIObject getParent() {
		return parent;
	}

	void initialize() {
		onInit();
		created = true;
	}

	final void updateDraw(Draw draw) {
		this.draw = draw;

		//while(when_ready_funcs.size() > 0)
		//	when_ready_funcs.remove(0).run();

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

	protected void sendEvent(IEvent event) {
		throw new RuntimeException("Not implemented yet");
	}

	/**
	 * Evaluates to true if this object has been drawn once or more, so that translation
	 * and other initializing has been done.
	 * You need to check this before calling functions like getPixelDimension etc, as they are
	 * not available until the object has been drawn (and initialized).
	 */
	public boolean isInitialized() {
		return created;
	}

	/**
	 * Add your function here to run it after this object has been drawn for the first time
	 */
	public void whenReady(Runnable func) {
		when_ready_funcs.add(func);
	}

	public boolean isAlive() {
		return alive;
	}
}
