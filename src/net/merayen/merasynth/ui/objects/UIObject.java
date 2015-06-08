package net.merayen.merasynth.ui.objects;

import net.merayen.merasynth.ui.TranslationData;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.util.Draw;

public abstract class UIObject {
	public UIObject parent;
	
	// Outline of the box this UIObject has drawn on. Used to figure out mouse capture
	public float draw_x = 0;
	public float draw_y = 0;
	public float draw_width = 0;
	public float draw_height = 0;
	
	public int current_z_index; // The current *drawn* Z index of this UIObject
	
	public TranslationData translation = new TranslationData();
	public TranslationData absolute_translation;
	
	protected net.merayen.merasynth.ui.DrawContext draw_context;
	protected Draw draw; // Helper class to draw stuff
	
	boolean created = false;
	
	protected void onCreate() {
		/*
		 * Overload this one to initialize when graphic is created.
		 * No drawing is performed here, only initialization of eventually children UIObject()s +++
		 */
	}
	
	protected abstract void onDraw(java.awt.Graphics2D g);
	
	public final void update(net.merayen.merasynth.ui.DrawContext dc) {
		if(!created) {
			onCreate();
			created = true;
		}
		
		this.draw_context = dc;
		
		this.draw = new Draw(this, dc.graphics2d);
		
		draw_context.translation.push(translation);
		
		absolute_translation = draw_context.translation.getCurrentTranslationData(); // Caching for outside use
		current_z_index = draw_context.pushZCounter();
		
		for(IEvent event : draw_context.incoming_events)
			receiveEvent(event);
		
		onDraw(dc.graphics2d);
		
		// Copy drawn outline box. For caching, drawing, mouse events etc
		this.draw_x = draw.draw_outline.x;
		this.draw_y = draw.draw_outline.y;
		this.draw_width = draw.draw_outline.width;
		this.draw_height = draw.draw_outline.height;
		
		draw_context.translation.pop();
	}
	
	protected void drawObject(UIObject obj) {
		obj.update(draw_context);
	}
	
	public java.awt.Point getAbsolutePixelPoint(float offset_x, float offset_y) { // Absolute position
		TranslationData td = absolute_translation;
		return new java.awt.Point((int)((draw_context.width * td.scale_x) * (td.x + offset_x)), (int)((draw_context.height * td.scale_y) * (td.y + offset_y)));
	}
	
	public net.merayen.merasynth.ui.Point getAbsolutePosition() {
		/*
		 * Returns the absolute position of the node.
		 * Does not include any scrolling that happens when we are moving
		 */
		TranslationData td = absolute_translation;
		return new net.merayen.merasynth.ui.Point(td.x, td.y);
	}
	
	public net.merayen.merasynth.ui.Point getAbsolutePointFromPixel(int x, int y) { // TODO implement scaling
		/*
		 * Convert pixel coordinates to our coordinate system
		 */
		TranslationData td = absolute_translation;
		return new net.merayen.merasynth.ui.Point((float)x / (draw_context.width * td.scale_x), (float)y / (draw_context.height * td.scale_y));
	}
	
	public net.merayen.merasynth.ui.Point getPointFromPixel(int x, int y) {
		/*
		 * Get our internal (relative) position from absolute window pixel position.
		 */
		TranslationData td = absolute_translation;
		return new net.merayen.merasynth.ui.Point((float)x / (draw_context.width * td.scale_x) - td.x, ((float)y / (draw_context.height * td.scale_y) - td.y));
	}

	public java.awt.Dimension getPixelDimension(float width, float height) {
		TranslationData td = absolute_translation;
		return new java.awt.Dimension((int)(draw_context.width * td.scale_x * width), (int)(draw_context.height * td.scale_y * height));
	}
	
	public int convertUnitToPixel(float a) {
		/*
		 * Converts a single unit.
		 * Uses both scale_x and scale_y to figure out the resulting value.
		 */
		TranslationData td = absolute_translation;
		float resolution = Math.min(draw_context.width, draw_context.height);
		return (int)(a * resolution * ((td.scale_x + td.scale_y) / 2f));
	}
	
	protected void onEvent(IEvent e) {
		// Override me, I'm worth nothing in this world! :D
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
}
