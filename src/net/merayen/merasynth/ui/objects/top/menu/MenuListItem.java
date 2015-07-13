package net.merayen.merasynth.ui.objects.top.menu;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.objects.Group;
import net.merayen.merasynth.ui.util.MouseHandler;

public class MenuListItem extends Group {
	public static abstract class Handler {
		public void onClick() {}
	}

	public String label = "";
	public float width = 1f; // Set by MenuList()
	private float label_width;
	private MouseHandler mouse_handler;
	private Handler handler;
	private boolean over;

	protected void onInit() {
		mouse_handler = new MouseHandler(this);
		mouse_handler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseClick(Point position) {
				if(handler != null)
					handler.onClick();
			}

			@Override
			public void onMouseOver() {
				over = true;
			}

			@Override
			public void onMouseOut() {
				over = false;
			}
		});
	}

	protected void onDraw() {
		draw.setFont("Geneva", 1.2f);
		label_width = draw.getTextWidth(label);

		if(over)
			draw.setColor(150, 150, 150);
		else
			draw.setColor(80, 80, 80);
		draw.fillRect(0.1f, 0.1f, width, getMenuItemHeight() - 0.2f);

		draw.setColor(50, 50, 50);
		draw.text(label, 1f, 1.5f);

		draw.setColor(200, 200, 200);
		draw.text(label, 1.05f, 1.45f);

		super.onDraw();
	}

	protected void onEvent(IEvent e) {
		mouse_handler.handle(e);
	}

	public float getMenuItemWidth() {
		return label_width + 3f;
	}

	public float getMenuItemHeight() {
		return 2f;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
