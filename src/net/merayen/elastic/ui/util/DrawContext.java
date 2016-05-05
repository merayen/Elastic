package net.merayen.elastic.ui.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.merayen.elastic.ui.TranslationData;
import net.merayen.elastic.ui.TranslationDataStack;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;

/**
 * Session-like class for the current drawing.
 * Class is thrown away between every repainting.
 * Object is thrown between all the UIObject()s
 */
public class DrawContext {
	public final java.awt.Graphics2D graphics2d; 
	public final int width;
	public final int height;

	private int z_counter = 0;

	public final List<IEvent> incoming_events;

	//private final HitTester hit_tester = new HitTester(); 

	public final TranslationDataStack translation_stack = new TranslationDataStack();

	/**
	 * Keeps track of where in the tree we are drawing.
	 * Note, this does not represent how the actual UIObject tree, only
	 * how it is being drawn, so that the current object can inspect the
	 * drawn hierarchy when it is being drawn.
	 */
	private final LinkedList<UIObject> draw_stack = new LinkedList<>();

	public DrawContext(java.awt.Graphics2D graphics2d, int width, int height, List<IEvent> events) {
		this.graphics2d = graphics2d;
		this.width = width;
		this.height = height;
		this.incoming_events = events;
	}

	public void push(UIObject uiobject) {
		if(draw_stack.contains(uiobject))
			throw new RuntimeException("Cyclic drawing of object detected");

		draw_stack.addLast(uiobject);
		translation_stack.push(uiobject.translation);
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
}
