package net.merayen.elastic.ui.objects.top.viewport;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.MutablePoint;

public class ViewportDrag extends UIObject {
	public interface Handler {
		void onStartDrag(float diff, boolean vertical);
		void onDrag(float diff, boolean vertical);
		void onDrop(float diff, boolean vertical);
	}

	float width, height;
	private Handler handler;
	private MouseHandler mousehandler;
	private boolean drag_x, drag_y;
	private static int lol_c;
	private int lol = lol_c++;

	ViewportDrag(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onInit() {
		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDrag(MutablePoint start, MutablePoint offset) {
				if(!drag_x && !drag_y) {
					if(offset.getX() <= -10) {
						drag_x = true;
						handler.onStartDrag(offset.getX(), true);
					} else if(offset.getY() <= -10) {
						drag_y = true;
						handler.onStartDrag(offset.getY(), false);
					}

				} else if(drag_x) {
					if(offset.getX() > -10) {
						//drag_x = false;
						handler.onDrag(0, true);
					} else {
						handler.onDrag(offset.getX(), true);
					}

				} else if(drag_y) {
					if(offset.getY() > -10) {
						//drag_y = false;
						handler.onDrag(0, false);
					} else {
						handler.onDrag(offset.getY(), false);
					}
				}
			}

			@Override
			public void onMouseDrop(MutablePoint start, MutablePoint offset) {
				if(drag_x)
					handler.onDrop(offset.getX(), true);
				else if(drag_y)
					handler.onDrop(offset.getY(), false);

				drag_x = false;
				drag_y = false;
			}
		});
	}

	@Override
	public void onDraw(Draw draw) {
		draw.setColor(150, 150, 150); // Move out to separate UIObject, make interactable
		draw.setStroke(1);
		for(int i = 0; i < 5; i++)
			draw.line(width - i * 5 - 2, height - 2, width - 2, height - i * 5 - 2);
	}

	@Override
	public void onEvent(UIEvent event) {
		mousehandler.handle(event);
	}
}
