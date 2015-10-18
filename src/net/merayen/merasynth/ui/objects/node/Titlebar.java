package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.util.Moveable;

public class Titlebar extends UIGroup {
	public float width;
	public String title = "";

	private Moveable moveable = null;

	protected void onInit() {
		moveable = new Moveable(this.parent, this);
		moveable.setHandler(new Moveable.IMoveable() {

			@Override
			public void onGrab() {}

			@Override
			public void onMove() {}

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

	protected void onEvent(net.merayen.merasynth.ui.event.IEvent event) {
		moveable.handle(event);
	}
}
