package net.merayen.elastic.ui.util;

import net.merayen.elastic.ui.TranslationDataStack;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.surface.Surface;
import net.merayen.elastic.util.MutablePoint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Session-like class for the current drawing.
 * Class is thrown away between every repainting.
 * Object is thrown between all the UIObject()s
 */
public class DrawContext {
	public final java.awt.Graphics2D graphics2d;
	public final MutablePoint windowLocation;
	private final Surface surface;
	private int z_counter = 0;
	public final List<UIEvent> incoming_events;
	public final TranslationDataStack translation_stack = new TranslationDataStack();

	/**
	 * Keeps track of where in the tree we are drawing.
	 * Note, this does not represent how the actual UIObject tree, only
	 * how it is being drawn, so that the current object can inspect the
	 * drawn hierarchy when it is being drawn.
	 */
	private final LinkedList<UIObject> draw_stack = new LinkedList<>();

	public DrawContext(java.awt.Graphics2D graphics2d, Surface surface, List<UIEvent> events) { // TODO abstract away Graphics2D
		this.graphics2d = graphics2d;
		this.windowLocation = surface.getSurfaceLocation();

		this.surface = surface;
		this.incoming_events = events;
	}

	public void push(UIObject uiobject) {
		if(draw_stack.contains(uiobject))
			throw new RuntimeException("Cyclic drawing of object detected");

		draw_stack.addLast(uiobject);

		translation_stack.push(uiobject.getTranslation()); // XXX occasionally NullPointerException here
	}

	/**
	 * Only used by the UIObject
	 */
	public void pop() {
		draw_stack.removeLast();
		translation_stack.pop();
	}

	public List<UIObject> getDrawStack() {
		return new ArrayList<>(draw_stack);
	}

	public int pushZIndex() {
		return ++z_counter;
	}

	public String getSurfaceID() {
		return surface.getId();
	}
}
