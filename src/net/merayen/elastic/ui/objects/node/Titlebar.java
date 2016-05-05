package net.merayen.elastic.ui.objects.node;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.util.Movable;

class Titlebar extends UIObject {
	public float width;
	public String title = "";

	private Movable moveable = null;

	protected void onInit() {
		moveable = new Movable(this.getParent(), this);
		moveable.setHandler(new Movable.IMoveable() {

			@Override
			public void onGrab() {}

			@Override
			public void onMove() {
				((UINode)getParent()).sendParameter("ui.java.translation.x", getParent().translation.x);
				((UINode)getParent()).sendParameter("ui.java.translation.y", getParent().translation.y);
			}

			@Override
			public void onDrop() {}
		});
	}

	protected void onDraw() {
		draw.setColor(30, 30, 30);
		draw.fillRect(2f, 2f, width - 4f, 10f);

		draw.setColor(200, 200, 200);
		draw.setFont("Verdana", 8f);
		draw.text(title, 5f, 9f);

		super.onDraw();
	}

	protected void onEvent(net.merayen.elastic.ui.event.IEvent event) {
		moveable.handle(event);
	}
}
