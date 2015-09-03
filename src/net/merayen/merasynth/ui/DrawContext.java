package net.merayen.merasynth.ui;

import java.util.ArrayList;

import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.util.ClipStack;

public class DrawContext {
	/*
	 * Session-like class for the current drawing.
	 * Class is thrown away between every repainting.
	 * Object is thrown between all the UIObject()s
	 */
	public final java.awt.Graphics2D graphics2d; 
	public final int width;
	public final int height;

	private int z_counter = 0;

	public ArrayList<IEvent> incoming_events;
	public ArrayList<IEvent> outgoing_events = new ArrayList<IEvent>(); // Events that are sent out (and will be sent back in again in next redraw)

	public ClipStack clip_stack = new ClipStack(); // Global clip stack

	public final net.merayen.merasynth.ui.TranslationDataStack translation_stack = new net.merayen.merasynth.ui.TranslationDataStack();

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

	public int pushZCounter() {
		/*
		 * Increments the z_counter.
		 * Do this for every UIObject that is being drawn. 
		 */
		return ++z_counter;
	}
}
