package net.merayen.merasynth.ui.objects.node;

import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.util.Moveable;

public class Titlebar extends UIGroup {
	public float width;
	public String title = "";

	private Moveable moveable = null;
	int i;

	protected void onInit() {
		moveable = new Moveable(this.parent, this);
		moveable.setHandler(new Moveable.IMoveable() {

			@Override
			public void onGrab() {
				System.out.println("Grabbed");
			}

			@Override
			public void onMove() {}

			@Override
			public void onDrop() {}
		});
	}

	protected void onDraw() {
		draw.setColor(30, 30, 30);
		draw.fillRect(0.2f, 0.2f, width - 0.4f, 1f);

		draw.setColor(200, 200, 200);
		draw.setFont("Verdana", 0.8f);
		draw.text(title, 0.5f, 0.9f);

		//if(this.parent instanceof net.merayen.merasynth.client.ui_test.UI && (i++%10) == 0)
		//	System.out.printf("%s\n%s\n", draw_context.translation_stack, absolute_translation);

		super.onDraw();
	}

	protected void onEvent(net.merayen.merasynth.ui.event.IEvent event) {
		moveable.handle(event);
	}
}
