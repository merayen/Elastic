package net.merayen.elastic.ui.util;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.event.MouseEvent;

/**
 * Helper class to make an UIObject moveable.
 * Tightly coupled to UIObjects.
 */
public class MouseHandler {
	public static abstract class Handler {
		public void onMouseDown(net.merayen.elastic.util.MutablePoint position) {} // Mouse down on the hitbox
		public void onMouseUp(net.merayen.elastic.util.MutablePoint position) {} // Mouse up (if inside the hitbox)
		public void onMouseClick(net.merayen.elastic.util.MutablePoint position) {} // Mouse clicked down and up in the hitbox
		public void onMouseOver() {} // Mouse enters the hitbox
		public void onMouseOut() {} // Mouse leaves the hitbox
		public void onMouseMove(net.merayen.elastic.util.MutablePoint position) {} // Mouse moves over the hitbox
		public void onMouseDrag(net.merayen.elastic.util.MutablePoint position, net.merayen.elastic.util.MutablePoint offset) {} // Mouse down on hitbox and is now moving
		public void onMouseDrop(net.merayen.elastic.util.MutablePoint position, net.merayen.elastic.util.MutablePoint offset) {} // Mouse has dragged item but now drops it
		public void onGlobalMouseMove(net.merayen.elastic.util.MutablePoint global_position) {} // Mouse has been moved anywhere. TODO add others too? Like click
		public void onGlobalMouseUp(net.merayen.elastic.util.MutablePoint global_position) {}
		public void onMouseOutsideDown(net.merayen.elastic.util.MutablePoint global_position) {} // Mouse down outside
	}

	private Handler handler_class;
	protected UIObject uiobject;

	private net.merayen.elastic.util.MutablePoint drag_start;
	private net.merayen.elastic.util.MutablePoint current_absolute;

	private boolean mouse_down = false;
	private boolean mouse_over = false;
	private boolean mouse_dragging = false;

	private MouseEvent currentMouseEvent;

	private final MouseEvent.Button button; // Which button on mouse to react on

	public MouseHandler(UIObject uiobject) {
		this.uiobject = uiobject;
		this.button = null;
	}

	public MouseHandler(UIObject uiobject, MouseEvent.Button button) {
		this.uiobject = uiobject;
		this.button = button;
	}

	public void setHandler(Handler cls) {
		this.handler_class = cls;
	}

	/**
	 * Call this from your UIObject to handle event
	 */
	public void handle(UIEvent event) { // XXX Should we ensure UIObject is initialized before
		if (!uiobject.isInitialized())
			return;

		if(event instanceof MouseEvent) {
			MouseEvent e = (MouseEvent)event;
			currentMouseEvent = e;

			if(button != null && e.button != null && e.button != button)
				return;

			net.merayen.elastic.util.MutablePoint p_relative = uiobject.getRelativeFromAbsolute(e.x, e.y);
			net.merayen.elastic.util.MutablePoint p_absolute = new net.merayen.elastic.util.MutablePoint(e.x, e.y);

			boolean hit = e.isHit(uiobject); // XXX doing this for absolute every uiobject, ouch!

			if(e.action == MouseEvent.Action.DOWN) {
				if(hit) {
					mouse_down = true;
					handler_class.onMouseDown(p_relative);

					drag_start = new net.merayen.elastic.util.MutablePoint(p_relative);
				} else {
					handler_class.onMouseOutsideDown(p_absolute);
				}
			}
			else if(e.action == MouseEvent.Action.UP) {
				if(mouse_dragging && mouse_down) {
					mouse_dragging = false;
					handler_class.onMouseDrop(p_relative, new net.merayen.elastic.util.MutablePoint(p_relative.getX() - drag_start.getX(), p_relative.getY() - drag_start.getY()));
				}

				if(hit) {
					handler_class.onMouseUp(p_relative);
					if(mouse_down)
						handler_class.onMouseClick(p_relative);
				}

				handler_class.onGlobalMouseUp(p_absolute);

				mouse_down = false;
			}
			else if(e.action == MouseEvent.Action.MOVE) {
				if(hit) {
					handler_class.onMouseMove(p_relative);

					if(!mouse_over) {
						mouse_over = true;
						handler_class.onMouseOver();
					}
				}

				handler_class.onGlobalMouseMove(p_absolute);

				if(!hit && mouse_over) {
					mouse_over = false;
					handler_class.onMouseOut();
				}

				if(mouse_down) {
					mouse_dragging = true;
					handler_class.onMouseDrag(p_relative, new net.merayen.elastic.util.MutablePoint(p_relative.getX() - drag_start.getX(), p_relative.getY() - drag_start.getY()));
				}
			}
		}

		currentMouseEvent = null;
	}

	/**
	 * Retrieve current mouse event. Only available when inside one of the Handler callbacks
	 */
	public MouseEvent getMouseEvent() {
		return currentMouseEvent;
	}

	public boolean isDown() {
		return mouse_down;
	}
}
