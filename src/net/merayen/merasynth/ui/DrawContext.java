package net.merayen.merasynth.ui;

import java.util.ArrayList;
import net.merayen.merasynth.ui.event.IEvent;

public class DrawContext {
	/*
	 * Stupid class containing the current drawing context.
	 * Object is thrown between all the UIObject()s
	 */
	
	public final java.awt.Graphics2D graphics2d; 
	public final int width;
	public final int height;
	
	public ArrayList<IEvent> incoming_events;
	public ArrayList<IEvent> outgoing_events = new ArrayList<IEvent>(); // Events that are sent out (and will be sent back in again in next redraw)
	
	public final net.merayen.merasynth.ui.TranslationDataStack translation = new net.merayen.merasynth.ui.TranslationDataStack();
	
	public DrawContext(java.awt.Graphics2D graphics2d, ArrayList<IEvent> incoming_events, int width, int height) {
		this.graphics2d = graphics2d;
		this.width = width;
		this.height = height;
		this.incoming_events = incoming_events;
	}
	
	public void queueEvent(IEvent event) {
		/*
		 * Queue event for next redraw
		 */
		outgoing_events.add(event);
	}
}
