package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.UIClip;
import net.merayen.elastic.ui.util.Movable;

public class Scroll extends UIObject {
	private class Bar extends UIObject {
		//private MouseHandler mousehandler;
		private Movable movable;
		private boolean x;

		Bar(boolean x) {
			this.x = x;
		}

		@Override
		protected void onInit() {
			/*mousehandler = new MouseHandler(this, Button.LEFT);
			mousehandler.setHandler(new MouseHandler.Handler() {
				private float start = Float.MIN_VALUE;

				@Override
				public void onMouseDrag(Point position, Point offset) {
					if(start == Float.MIN_NORMAL)
						start = ;

					set(start + (x ? offset.x : offset.y));
					System.out.println(offset.y);
				}

				@Override
				public void onGlobalMouseUp(Point global_position) {
					start = Float.MIN_NORMAL;
				}
			});*/
			//UIObject self = this;
			movable = new Movable(this, this);

			if(x) movable.drag_scale_y = 0;
			else movable.drag_scale_x = 0;

			movable.setHandler(new Movable.IMoveable() {
				@Override
				public void onMove() {
					updateFromBars();
				}

				@Override
				public void onGrab() {}

				@Override
				public void onDrop() {}
			});
		}

		@Override
		protected void onDraw() {
			draw.setColor(255, 0, 255);
			draw.fillRect(0, 0, bar_width, bar_width);
		}

		@Override
		protected void onEvent(IEvent e) {
			//mousehandler.handle(e);
			movable.handle(e);
		}

		private float get() {
			return x ? container.translation.x : container.translation.y;
		}

		private void set(float v) {
			if(x)
				container.translation.x = v;
			else
				container.translation.y = v;
		}
	}

	public float width = 100;
	public float height = 100;
	public float content_width = 0;
	public float content_height = 0;
	public float bar_width = 10;
	public final UIObject container = new UIObject();
	Scroll self = this;

	private final UIClip clip = new UIClip();
	private final Bar bar_x = new Bar(true);
	private final Bar bar_y = new Bar(false);

	@Override
	protected void onInit() {
		clip.add(container);
		add(clip);
		updateBars();
	}

	@Override
	protected void onDraw() {
		draw.setColor(0, 0, 0);
		draw.setStroke(bar_width);
		draw.fillRect(0, height - bar_width, width, bar_width);
		draw.fillRect(width - bar_width, 0, bar_width, height);

		draw.setColor(255, 0, 255);
	}

	@Override
	protected void onUpdate() {
		clip.width = width - bar_width;
		clip.height = height - bar_width;

		if(container.translation.x > 0)
			container.translation.x = 0;

		if(container.translation.y > 0)
			container.translation.y = 0;

		if(container.translation.x < -(content_width - width) && content_width > width)
			container.translation.x = -(content_width - width);

		if(container.translation.y < -(content_height - height) && content_height > height)
			container.translation.y = -(content_height - height);

		bar_x.translation.y = height - bar_width;
		bar_y.translation.x = width - bar_width;
	}

	private void updateBars() {
		if(content_width - width > 0) {
			if(bar_x.getParent() == null)
				add(bar_x);

			float x = ((-container.translation.x) / (content_width - clip.width));
			bar_x.translation.x = x * (width - bar_width);
			//bar_x.translation.y = height - bar_width;
		} else if(bar_x.getParent() != null) {
			remove(bar_x);
		}

		if(content_height - height > 0) {
			if(bar_y.getParent() == null)
				add(bar_y);

			float y = ((-container.translation.y) / (content_height - clip.height));
			bar_y.translation.x = width - bar_width;
			//bar_y.translation.y = y * (height - bar_width);
		} else if(bar_y.getParent() != null) {
			remove(bar_y);
		}
	}

	private void updateFromBars() {
		bar_x.translation.x = Math.max(0, Math.min(width - bar_width, bar_x.translation.x));
		bar_y.translation.y = Math.max(0, Math.min(height - bar_width, bar_y.translation.y));

		container.translation.x = ((bar_x.translation.x / (width - bar_width)) * -(content_width - width));
		container.translation.y = ((bar_y.translation.y / (height - bar_width)) * -(content_height - height));
	}
}
