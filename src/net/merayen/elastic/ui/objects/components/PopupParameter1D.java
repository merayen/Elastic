package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.framework.PopupParameter;

/**
 * Presents UI for a single parameter.
 */
public class PopupParameter1D extends UIObject { // TODO rename to PopupParameter
	public interface Handler {
		public void onMove(float value);
		public void onChange(float value);
	}

	private class Window extends UIObject {
		@Override
		protected void onDraw() {
			draw.setColor(150, 150, 150);
			draw.fillRect(-20, 0, 20, popup_height);

			draw.setColor(50, 50, 50);
			draw.rect(-20, 0, 20, popup_height);
		}
	}

	private final PopupParameter box;
	private final UIObject window;
	private final UIObject window_container; // Only for offsetting the popup

	private Handler handler;

	public float popup_height = 200f;
	public final BoxLabel label = new BoxLabel("###");

	public float drag_scale = 1f;

	public PopupParameter1D() {
		window_container = new UIObject();
		window = new Window();
		window_container.add(window);

		box = new PopupParameter(label, window_container);
		box.setHandler(new PopupParameter.Handler() {

			@Override
			public void onMove() {
				box.popup.translation.x = 0; // Constrain X-axis
				if(handler != null)
					//handler.onMove(box.popup.translation.y / popup_height);
					handler.onMove(box.getY());
			}

			@Override
			public void onGrab() {}

			@Override
			public void onDrop() {
				if(handler != null)
					handler.onChange(box.popup.translation.y / popup_height);
			}
		});

		box.popup_height = popup_height;
	}

	@Override
	protected void onInit() {
		add(box);
	}

	@Override
	protected void onUpdate() {
		box.popup_height = popup_height;
		window.translation.y = label.getHeight() / 2;
		box.drag_scale_y = drag_scale;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public UIObject getPane() {
		return box.minified;
	}

	public void setValue(float value) {
		box.setY(value);
	}

	public float getValue() {
		return box.getY();
	}
}
