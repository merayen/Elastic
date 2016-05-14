package net.merayen.elastic.ui.objects.top.viewport;

import net.merayen.elastic.ui.Point;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.ui.util.MouseHandler;

public class ViewportDrag extends UIObject {
	public interface Handler {
		public void onStartDrag(float diff, boolean vertical);
		public void onDrag(float diff, boolean vertical);
		public void onDrop(float diff, boolean vertical);
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
	protected void onInit() {
		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDrag(Point start, Point offset) {
				if(!drag_x && !drag_y) {
					if(offset.x <= -10) {
						drag_x = true;
						handler.onStartDrag(offset.x, true);
					} else if(offset.y <= -10) {
						drag_y = true;
						handler.onStartDrag(offset.y, false);
					}

				} else if(drag_x) {
					if(offset.x > -10) {
						//drag_x = false;
						handler.onDrag(0, true);
					} else {
						handler.onDrag(offset.x, true);
					}

				} else if(drag_y) {
					if(offset.y > -10) {
						//drag_y = false;
						handler.onDrag(0, false);
					} else {
						handler.onDrag(offset.y, false);
					}
				}
			}

			@Override
			public void onMouseDrop(Point start, Point offset) {
				if(drag_x)
					handler.onDrop(offset.x, true);
				else if(drag_y)
					handler.onDrop(offset.y, false);

				drag_x = false;
				drag_y = false;
			}
		});
	}

	@Override
	protected void onDraw() {
		draw.setColor(150, 150, 150); // Move out to separate UIObject, make interactable
		draw.setStroke(1);
		for(int i = 0; i < 5; i++)
			draw.line(width - i * 5 - 2, height - 2, width - 2, height - i * 5 - 2);
	}

	@Override
	protected void onUpdate() {
		((Top)search.getTop()).debugPrint("ViewportDrag outline() " + lol, this.outline_abs_px);
	}

	@Override
	protected void onEvent(IEvent event) {
		mousehandler.handle(event);
	}
}