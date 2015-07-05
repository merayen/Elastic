package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.util.Moveable;

public class Titlebar extends Group {
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
		draw.setColor(80, 80, 80);
		draw.fillRect(0.2f, 0.2f, width - 0.4f, 1f);

		draw.setColor(200, 200, 200);
		draw.setFont("Verdana", 0.8f);
		draw.text(title, 0.5f, 0.9f);

		super.onDraw();
	}

	protected void onEvent(net.merayen.merasynth.ui.event.IEvent event) {
		moveable.handle(event);
	}
}
