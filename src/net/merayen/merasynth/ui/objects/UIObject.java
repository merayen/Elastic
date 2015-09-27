package net.merayen.merasynth.ui.objects;

import java.util.ArrayList;

import net.merayen.merasynth.ui.Rect;
import net.merayen.merasynth.ui.TranslationData;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.objects.top.Top;
import net.merayen.merasynth.ui.util.Draw;

public abstract class UIObject {
	public UIGroup parent;

	private String id = new Integer(java.util.UUID.randomUUID().hashCode()).toString();
	protected boolean serializable = false; // Set to true in your class to enable dumping and restoration of it

	// Outline of the box this UIObject has drawn on. Used to figure out mouse capture
	// Set by the Draw()-class
	public net.merayen.merasynth.ui.Rect outline_abs_px; // Absolute, in screen pixels

	public int draw_z; // The current *drawn* Z index of this UIObject. Retrieved by the counter from DrawContext()

	public TranslationData translation = new TranslationData();
	public TranslationData absolute_translation;

	protected net.merayen.merasynth.ui.DrawContext draw_context;
	protected Draw draw; // Helper class to draw stuff

	private boolean created = false;
	private ArrayList<Runnable> when_ready_funcs = new ArrayList<Runnable>();

	/*
	 * Overload this one to initialize when graphic is created.
	 * No drawing is performed here, only initialization of eventually children UIObject()s +++
	 */
	protected void onInit() {} 
	protected void onDraw() {}
	protected void onEvent(IEvent e) {}

	public void updateDraw(net.merayen.merasynth.ui.DrawContext dc) {
		if(!created)
			onInit();

		this.draw_context = dc;

		dc.translation_stack.push(translation);

		absolute_translation = dc.translation_stack.getAbsolute(); // Caching for outside use
		draw_z = dc.pushZCounter(); // Set the z-index we are drawing on

		if(absolute_translation.visible) {
			this.draw = new Draw(this, dc);

			onUpdateDraw();

			// Copy drawn outline box. For caching, drawing, mouse events etc
			this.outline_abs_px = draw.getAbsoluteOutline();

			this.draw.destroy();

			// Remove draw object, since we do not allow drawing outside of the onDraw-function
			this.draw = null;
		} else {
			this.outline_abs_px = new Rect();
		}

		draw_context.translation_stack.pop();

		created = true;

		// Run any functions that are waiting for this object to be created
		while(when_ready_funcs.size() > 0)
			when_ready_funcs.remove(0).run();
	}

	public final void updateEvents(net.merayen.merasynth.ui.DrawContext dc) {
		if(!created)
			return;

		this.draw_context = dc;
		onUpdateEvents();
	}

	protected void onUpdateDraw() {
		onDraw();
	}

	protected void onUpdateEvents() {
		if(draw_context != null)
			for(IEvent event : draw_context.incoming_events)
				onEvent(event);
	}

	public java.awt.Point getAbsolutePixelPoint(float offset_x, float offset_y) { // Absolute position incl scrolling
		TranslationData td = absolute_translation;
		//return new java.awt.Point((int)((draw_context.width / td.scale_x) * (td.x + offset_x)), (int)((draw_context.height / td.scale_y) * (td.y + offset_y)));
		//return new java.awt.Point((int)((td.x + offset_x) / td.scale_x), (int)((td.y + offset_y) / td.scale_y));
		return new java.awt.Point((int)(td.x + offset_x / td.scale_x), (int)(td.y + offset_y / td.scale_y)); // Pixel perfect
	}

	public net.merayen.merasynth.ui.Point getAbsolutePosition() {
		/*
		 * Returns the absolute position of the node.
		 */
		TranslationData td = absolute_translation;
		return new net.merayen.merasynth.ui.Point(td.x, td.y);
	}

	/*
	 * Returns the relative position of the object "obj" to this object.
	 */
	public net.merayen.merasynth.ui.Point getRelativePosition(UIObject obj) {
		TranslationData td1 = absolute_translation;
		return new net.merayen.merasynth.ui.Point(
			obj.absolute_translation.x - absolute_translation.x,
			obj.absolute_translation.y - absolute_translation.y
		);
	}

	public net.merayen.merasynth.ui.Point getAbsolutePointFromPixel(int x, int y) { // TODO implement scaling
		/*
		 * Convert pixel coordinates to our coordinate system
		 */
		TranslationData td = absolute_translation;
		return new net.merayen.merasynth.ui.Point(td.x + x * td.scale_x, td.y + y * td.scale_y);
	}

	public net.merayen.merasynth.ui.Point getPointFromPixel(int x, int y) {
		/*
		 * Get our internal (relative) position from absolute window pixel position.
		 */
		TranslationData td = absolute_translation;
		//return new net.merayen.merasynth.ui.Point((float)x / (draw_context.width / td.scale_x) - td.x, (float)y / (draw_context.height / td.scale_y) - td.y);
		return new net.merayen.merasynth.ui.Point((float)x - td.x, (float)y - td.y);
	}

	public java.awt.Dimension getPixelDimension(float width, float height) {
		TranslationData td = absolute_translation;
		//return new java.awt.Dimension((int)((draw_context.width / td.scale_x) * width), (int)((draw_context.height / td.scale_y) * height));
		return new java.awt.Dimension((int)(width / td.scale_x), (int)(height / td.scale_y));
		//return new java.awt.Dimension((int)(width), (int)(height));
	}

	public int convertUnitToPixel(float a) {
		/*
		 * Converts a single unit.
		 * Uses both scale_x and scale_y to figure out the resulting value.
		 */
		TranslationData td = absolute_translation;
		//float resolution = Math.min(draw_context.width, draw_context.height);
		//return (int)(a * resolution / ((td.scale_x + td.scale_y) / 2f));
		float resolution = Math.min(td.scale_x, td.scale_y);
		return (int)(a / resolution);
	}

	public float convertPixelToUnit(int a) { // TODO Only uses the x-scale. Maybe make two functions, one for X and one for Y, and one for both somehow?
		/*
		 * Converts a single unit.
		 * Uses both scale_x and scale_y to figure out the resulting value. No it doesn't.
		 */
		TranslationData td = absolute_translation;
		//float resolution = Math.min(draw_context.width, draw_context.height); // Doing it the silly way
		//return (float)a / (resolution / ((td.scale_x + td.scale_y) / 2f));

		return (float)a * td.scale_x;
	}

	protected void sendEvent(IEvent event) {
		/*
		 * An uiobject can send an event to other uiobjects/outside. 
		 */
		draw_context.outgoing_events.add(event);
	}

	public boolean isReady() {
		/*
		 * Evaluates to true if this object has been drawn once or more, so that translation
		 * and other initializing has been done.
		 * You need to check this before calling functions like getPixelDimension etc, as they are
		 * not available until the object has been drawn (and initialized).
		 */
		return created;
	}

	public void whenReady(Runnable func) {
		/*
		 * Add your function here to run it after this object has been drawn for the first time
		 */
		when_ready_funcs.add(func);
	}

	public Top getTopObject() {
		UIObject x = this;
		while(x.parent != null)
			x = x.parent;

		if(!(x instanceof Top))
			throw new RuntimeException("Topmost uiobject is not Top()");

		return (Top)x;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}
}
