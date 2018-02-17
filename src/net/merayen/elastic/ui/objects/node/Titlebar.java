package net.merayen.elastic.ui.objects.node;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.MouseEvent;
import net.merayen.elastic.ui.util.Movable;

public class Titlebar extends UIObject {
	public float width;
	public String title = "";

	private boolean dragging; // True when user is dragging the node

	private Movable moveable = null;

	Titlebar() {}

	protected void onInit() {
		moveable = new Movable(this.getParent(), this, MouseEvent.Button.LEFT);
		moveable.setHandler(new Movable.IMoveable() {

			@Override
			public void onGrab() {
				dragging = true;
			}

			@Override
			public void onMove() {
				((UINode)getParent()).sendParameter("ui.java.translation.x", getParent().translation.x);
				((UINode)getParent()).sendParameter("ui.java.translation.y", getParent().translation.y);
			}

			@Override
			public void onDrop() {
				dragging = false;
			}
		});

		add(new TitleBarContextMenu(this));
	}

	@Override
	protected void onDraw() {
		draw.setColor(30, 30, 30);
		draw.fillRect(2f, 2f, width - 4f, 10f);

		draw.setColor(200, 200, 200);
		draw.setFont("Verdana", 8f);
		draw.text(title, 5f, 9f);

		super.onDraw();
	}

	@Override
	protected void onEvent(net.merayen.elastic.ui.event.UIEvent event) {
		moveable.handle(event);
	}

	public boolean isDragging() {
		return dragging;
	}
}
