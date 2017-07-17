package net.merayen.elastic.ui.objects.components.framework;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.event.MouseEvent;
import net.merayen.elastic.ui.util.Movable;

public class PopupParameter extends UIObject {
	public interface Handler {
		public void onGrab();
		public void onMove();
		public void onDrop();
	}

	/**
	 * Decides how many pixels that the window can be moved up and down
	 */
	public float popup_width = 100f;
	public float popup_height = 100f;
	public float drag_scale_x = 1f;
	public float drag_scale_y = 1f;

	public final UIObject minified;
	public final UIObject popup;

	private Movable movable;

	private Handler handler;

	public PopupParameter(UIObject minified, UIObject window) {
		this.minified = minified;
		this.popup = window;
	}

	@Override
	protected void onInit() {
		movable = new Movable(popup, minified);
		movable.setHandler(new Movable.IMoveable() {
			@Override
			public void onMove() {
				// Constrain
				popup.translation.x = Math.max(-popup_width, Math.min(0, popup.translation.x));
				popup.translation.y = Math.max(-popup_height, Math.min(0, popup.translation.y));

				if(handler != null)
					handler.onMove();
			}

			@Override
			public void onGrab() {
				add(popup);

				if(handler != null)
					handler.onGrab();
			}

			@Override
			public void onDrop() {
				remove(popup);

				if(handler != null)
					handler.onDrop();
			}
		});

		add(minified);
	}

	@Override
	protected void onEvent(IEvent event) {
		movable.handle(event);
	}

	@Override
	protected void onUpdate() {
		movable.drag_scale_x = drag_scale_x;
		movable.drag_scale_y = drag_scale_y;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public float getX() {
		return -popup.translation.x / popup_width;
	}

	public void setX(float value) {
		popup.translation.x = -value * popup_width;
	}

	public float getY() {
		return -popup.translation.y  / popup_height;
	}

	public void setY(float value) {
		popup.translation.y = -value * popup_height;
	}
}
