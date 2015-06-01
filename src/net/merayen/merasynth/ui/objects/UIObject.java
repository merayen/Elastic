package net.merayen.merasynth.ui.objects;

import net.merayen.merasynth.ui.TranslationData;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.util.Draw;

public abstract class UIObject {
	public UIObject parent;
	
	public TranslationData translation = new TranslationData();
	
	protected net.merayen.merasynth.ui.DrawContext draw_context;
	protected Draw draw; // Helper class to draw stuff
	
	boolean created = false;
	
	protected void onCreate() {
		/*
		 * Overload this one to initialize when graphic is created.
		 * No drawing is performed here, only initialization of eventually children UIObject()s
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
		
		for(IEvent event : draw_context.incoming_events)
			receiveEvent(event);
		
		onDraw(dc.graphics2d);
		
		draw_context.translation.pop();
	}
	
	protected void drawObject(UIObject obj) {
		obj.update(draw_context);
	}
	
	public java.awt.Point getAbsolutePixelPoint(float offset_x, float offset_y) { // Absolute position
		TranslationData td = draw_context.translation.getCurrentTranslationData();
		return new java.awt.Point((int)((draw_context.width * td.scale_x) * (td.x + offset_x)), (int)((draw_context.height * td.scale_y) * (td.y + offset_y)));
	}
	
	public net.merayen.merasynth.ui.Point getAbsolutePosition() {
		/*
		 * Returns the absolute position of the node.
		 * Does not include any scrolling that happens when we are moving
		 */
		TranslationData td = draw_context.translation.getCurrentTranslationData();
		return new net.merayen.merasynth.ui.Point(td.x, td.y);
	}
	
	public net.merayen.merasynth.ui.Point getAbsolutePointFromPixel(int x, int y) { // TODO implement scaling
		/*
		 * Convert pixel coordinates to our coordinate system
		 */
		TranslationData td = draw_context.translation.getCurrentTranslationData();
		return new net.merayen.merasynth.ui.Point((float)x / (draw_context.width * td.scale_x), (float)y / (draw_context.height * td.scale_y));
	}
	
	public net.merayen.merasynth.ui.Point getPointFromPixel(int x, int y) {
		/*
		 * Get our internal (relative) position from absolute window pixel position.
		 */
		TranslationData td = draw_context.translation.getCurrentTranslationData();
		return new net.merayen.merasynth.ui.Point((float)x / (draw_context.width * td.scale_x) - td.x, ((float)y / (draw_context.height * td.scale_y) - td.y));
	}

	public java.awt.Dimension getPixelDimension(float width, float height) {
		TranslationData td = draw_context.translation.getCurrentTranslationData();
		return new java.awt.Dimension((int)(draw_context.width * td.scale_x * width), (int)(draw_context.height * td.scale_y * height));
	}
	
	public int convertUnitToPixel(float a) {
		/*
		 * Converts a single unit.
		 * Uses both scale_x and scale_y to figure out the resulting value.
		 */
		TranslationData td = draw_context.translation.getCurrentTranslationData();
		float resolution = Math.min(draw_context.width, draw_context.height);
		return (int)(a * resolution * ((td.scale_x + td.scale_y) / 2f));
	}
	
	protected void onEvent(IEvent e) {
		// Override me, I'm worth nothing in this world! :D
	}
	
	public void receiveEvent(IEvent e) {
		onEvent(e);
	}
}
