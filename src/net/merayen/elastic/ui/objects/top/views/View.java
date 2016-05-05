package net.merayen.elastic.ui.objects.top.views;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.event.MouseEvent;

public abstract class View extends UIObject {
	public float width = 100;
	public float height = 100;
	private boolean focused; // true when mouse is hovering over us, meaning we have the focus

	@Override
	protected void onDraw() {
		if(focused) {
			draw.setColor(255, 0, 255);
			draw.setStroke(4);
			draw.rect(1, 1, width - 2, height - 2);
		}
	}

	@Override
	protected void onEvent(IEvent event) {
		if(event instanceof MouseEvent) {
			MouseEvent e = (MouseEvent)event;
			focused = (e.hitDepth(this) > -1);
		}
	}

	/**
	 * See if mouse is on this view.
	 */
	public boolean isFocused() {
		return focused;
	}
}
