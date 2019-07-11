package net.merayen.elastic.ui.objects.components.midiroll;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Point;

class Tangent extends UIObject {
	interface Handler {
		void onDown();
		void onUp();
	}

	public float width, height;

	public boolean marked;

	private final Handler handler;
	private final boolean black;
	private MouseHandler mouse_handler;
	private boolean active;
	private boolean standby;

	Tangent(boolean black, Handler handler) {
		this.black = black;
		this.handler = handler;
	}

	@Override
	public void onInit() {
		mouse_handler = new MouseHandler(this);
		mouse_handler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDown(Point position) {
				active = true;
				handler.onDown();
			}

			@Override
			public void onMouseOver() {
				if(standby) {
					active = true;
					handler.onDown();
				}
			}

			@Override
			public void onMouseOut() {
				if(active) {
					active = false;
					handler.onUp();
				}
			}

			@Override
			public void onGlobalMouseUp(Point global_position) {
				if(active) {
					active = false;
					handler.onUp();
				}
				standby = false;
			}
		});
	}

	@Override
	public void onDraw(Draw draw) {
		if(active)
			draw.setColor(100, 100, 100);
		else if(black)
			draw.setColor(50, 50, 50);
		else
			draw.setColor(255, 255, 255);

		draw.fillRect(0, 0, width, height);

		if (marked) {
			draw.setColor(150, 150, 150);
			draw.setStroke(1f);
			draw.line(0, height / 2, width, height / 2);
		}
	}

	@Override
	public void onEvent(UIEvent event) {
		mouse_handler.handle(event);
	}

	public void goStandby() {
		standby = true;
	}
}
