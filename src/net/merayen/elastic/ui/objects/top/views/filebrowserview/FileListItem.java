package net.merayen.elastic.ui.objects.top.views.filebrowserview;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Point;

class FileListItem extends UIObject {
	interface Handler {
		public void onClick();
	}

	private MouseHandler mouse_handler = new MouseHandler(this);
	private boolean over;
	String label;
	private float width, height;
	private Handler handler;

	void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onInit() {
		mouse_handler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseOver() {
				over = true;
			}

			@Override
			public void onMouseOut() {
				over = false;
			}

			@Override
			public void onMouseClick(Point position) {
				if(handler != null)
					handler.onClick();
			}
		});
	}

	@Override
	public void onDraw(Draw draw) {
		if(label == null)
			return;

		draw.setFont("", 15);
		width = draw.getTextWidth(label);
		height = 15;

		if(over)
			draw.setColor(150, 150, 150);
		else 
			draw.setColor(50, 50, 50);
		draw.fillRect(-5, -5, width + 10, 15 + 10);

		draw.setColor(255, 255, 255);
		draw.text(label, 0, 10);
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public void onEvent(UIEvent e) {
		mouse_handler.handle(e);
	}
}
