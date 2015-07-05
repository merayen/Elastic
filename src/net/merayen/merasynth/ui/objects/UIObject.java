package net.merayen.merasynth.ui.objects;

import net.merayen.merasynth.ui.TranslationData;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.util.Draw;

public abstract class UIObject {
	public UIObject parent;

	private String id = new Integer(java.util.UUID.randomUUID().hashCode()).toString();
	
	// Outline of the box this UIObject has drawn on. Used to figure out mouse capture
	// Set by the Draw()-class
	public net.merayen.merasynth.ui.Rect outline; // Relative, in our internal coordinate system
	public net.merayen.merasynth.ui.Rect outline_abs; // Absolute, in screen pixels
	
	public int draw_z; // The current *drawn* Z index of this UIObject. Retrieved by the counter from DrawContext()
	
	public TranslationData translation = new TranslationData();
	public TranslationData absolute_translation;
	
	protected net.merayen.merasynth.ui.DrawContext draw_context;
	protected Draw draw; // Helper class to draw stuff
	
	boolean created = false;
	
	/*
	 * Overload this one to initialize when graphic is created.
	 * No drawing is performed here, only initialization of eventually children UIObject()s +++
	 */
	protected void onInit() {} 
	protected void onDraw(java.awt.Graphics2D g) {}
	protected void onEvent(IEvent e) {}
	
	public final void update(net.merayen.merasynth.ui.DrawContext dc) {
		if(!created) {
			onInit();
			created = true;
		}
		
		this.draw_context = dc;
		
		this.draw = new Draw(this, dc.graphics2d);
		
		draw_context.translation.push(translation);
		
		absolute_translation = draw_context.translation.getCurrentTranslationData(); // Caching for outside use
		draw_z = draw_context.pushZCounter(); // Set the z-index we are drawing on
		
		for(IEvent event : draw_context.incoming_events)
			receiveEvent(event);
		
		onDraw(dc.graphics2d);
		
		// Copy drawn outline box. For caching, drawing, mouse events etc
		this.outline = draw.getRelativeOutline();
		this.outline_abs = draw.getAbsoluteOutline();
		
		draw_context.translation.pop();
	}
	
	protected void drawObject(UIObject obj) {
		obj.update(draw_context);
	}
	
	public java.awt.Point getAbsolutePixelPoint(float offset_x, float offset_y) { // Absolute position incl scrolling
		TranslationData td = absolute_translation;
		return new java.awt.Point((int)((draw_context.width / td.scale_x) * (td.x + offset_x - td.scroll_x)), (int)((draw_context.height / td.scale_y) * (td.y + offset_y - td.scroll_y)));
	}
	
	public net.merayen.merasynth.ui.Point getAbsolutePosition() {
		/*
		 * Returns the absolute position of the node.
		 * Does not include any scrolling!
		 */
		TranslationData td = absolute_translation;
		return new net.merayen.merasynth.ui.Point(td.x, td.y);
	}
	
	public net.merayen.merasynth.ui.Point getAbsolutePointFromPixel(int x, int y) { // TODO implement scaling
		/*
		 * Convert pixel coordinates to our coordinate system
		 */
		TranslationData td = absolute_translation;
		return new net.merayen.merasynth.ui.Point((float)x / (draw_context.width / td.scale_x) + td.scroll_x, (float)y / (draw_context.height / td.scale_y) + td.scroll_y);
	}
	
	public net.merayen.merasynth.ui.Point getPointFromPixel(int x, int y) {
		/*
		 * Get our internal (relative) position from absolute window pixel position.
		 */
		TranslationData td = absolute_translation;
		return new net.merayen.merasynth.ui.Point((float)x / (draw_context.width / td.scale_x) - td.x + td.scroll_x, (float)y / (draw_context.height / td.scale_y) - td.y + td.scroll_y);
	}

	public java.awt.Dimension getPixelDimension(float width, float height) {
		TranslationData td = absolute_translation;
		return new java.awt.Dimension((int)((draw_context.width / td.scale_x) * width), (int)((draw_context.height / td.scale_y) * height));
	}
	
	public int convertUnitToPixel(float a) {
		/*
		 * Converts a single unit.
		 * Uses both scale_x and scale_y to figure out the resulting value.
		 */
		TranslationData td = absolute_translation;
		float resolution = Math.min(draw_context.width, draw_context.height);
		return (int)(a * resolution / ((td.scale_x + td.scale_y) / 2f));
	}
	
	protected void sendEvent(IEvent event) {
		/*
		 * An uiobject can send an event to other uiobjects/outside. 
		 */
		draw_context.outgoing_events.add(event);
	}
	
	public void receiveEvent(IEvent e) {
		onEvent(e);
	}
	
	public String getID() {
		return id;
	}
	
	public void setID(String id) {
		this.id = id;
	}
}
