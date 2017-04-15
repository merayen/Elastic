package net.merayen.elastic.ui.objects.top.menu;

import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Point;

public class MenuListItem extends UIObject {
	public static abstract class Handler {
		public void onClick() {}
	}

	public String label = "";
	public float width = 10f; // Set by MenuList()
	private float label_width;
	private MouseHandler mouse_handler;
	private Handler handler;
	private boolean over;

	@Override
	protected void onInit() {
		mouse_handler = new MouseHandler(this);
		mouse_handler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseUp(Point position) {
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
		draw.setFont("Geneva", 12f);
		label_width = draw.getTextWidth(label);

		if(over)
			draw.setColor(150, 150, 150);
		else
			draw.setColor(80, 80, 80);
		draw.fillRect(1f, 1f, width, getMenuItemHeight() - 2f);

		draw.setColor(50, 50, 50);
		draw.text(label, 10f, 15f);

		draw.setColor(200, 200, 200);
		draw.text(label, 10.5f, 14.5f);

		super.onDraw();
	}

	protected void onEvent(IEvent e) {
		mouse_handler.handle(e);
	}

	public float getMenuItemWidth() {
		return label_width + 30f;
	}

	public float getMenuItemHeight() {
		return 20f;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
