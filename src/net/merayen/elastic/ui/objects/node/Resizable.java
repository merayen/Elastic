package net.merayen.elastic.ui.objects.node;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.event.MouseEvent.Button;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Point;

public class Resizable extends UIObject {
	public interface Handler {
		public void onResize();
	}

	private final Handler handler;
	private final UINode node;
	private MouseHandler mouse_handler;

	private float start_width, start_height;

	public Resizable(UINode node, Handler handler) {
		this.node = node;
		this.handler = handler;
	}

	@Override
	protected void onInit() {
		mouse_handler = new MouseHandler(this, Button.LEFT);
		mouse_handler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDown(Point position) {
				start_width = node.width;
				start_height = node.height;
			}

			@Override
			public void onMouseDrag(Point position, Point offset) {
				node.width = start_width + offset.x;
				node.height = start_height + offset.y;
				handler.onResize();
			}
		});
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		draw.setColor(200, 200, 200);
		draw.setStroke(0.5f);
		for(int i = 1; i < 8; i+=2)
			draw.line(node.width - 2 * i, node.height, node.width, node.height - 2 * i);
	}

	@Override
	protected void onEvent(UIEvent e) {
		mouse_handler.handle(e);
	}
}
