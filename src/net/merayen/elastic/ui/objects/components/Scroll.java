package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.UIClip;
import net.merayen.elastic.ui.util.Movable;

public class Scroll extends UIObject {
	private class Bar extends UIObject {
		private Movable movable;
		private boolean x;

		Bar(boolean x) {
			this.x = x;
		}

		@Override
		protected void onInit() {
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
			movable.handle(e);
		}
	}

	public float width = 100;
	public float height = 100;
	public float bar_width = 10;
	private final UIObject object;
	Scroll self = this;

	private final UIClip clip = new UIClip();
	private final Bar bar_x = new Bar(true);
	private final Bar bar_y = new Bar(false);

	private float content_width, content_height;

	public Scroll(UIObject object) {
		this.object = object;
	}

	@Override
	protected void onInit() {
		clip.add(object);
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
		content_width = object.getWidth();
		content_height = object.getHeight();

		System.out.println(content_width + " " + content_height);

		clip.width = width - bar_width;
		clip.height = height - bar_width;

		if(object.translation.x > 0)
			object.translation.x = 0;

		if(object.translation.y > 0)
			object.translation.y = 0;

		if(object.translation.x < -(content_width - width) && content_width > width)
			object.translation.x = -(content_width - width);

		if(object.translation.y < -(content_height - height) && content_height > height)
			object.translation.y = -(content_height - height);

		bar_x.translation.y = height - bar_width;
		bar_y.translation.x = width - bar_width;

		updateBars();
	}

	private void updateBars() {
		if(content_width - width > 0) {
			if(bar_x.getParent() == null)
				add(bar_x);

			bar_x.translation.y = height - bar_width;
		} else if(bar_x.getParent() != null) {
			remove(bar_x);
		}

		if(content_height - height > 0) {
			if(bar_y.getParent() == null)
				add(bar_y);

			bar_y.translation.x = width - bar_width;
		} else if(bar_y.getParent() != null) {
			remove(bar_y);
		}
	}

	private void updateFromBars() {
		bar_x.translation.x = Math.max(0, Math.min(width - bar_width, bar_x.translation.x));
		bar_y.translation.y = Math.max(0, Math.min(height - bar_width, bar_y.translation.y));

		object.translation.x = ((bar_x.translation.x / (width - bar_width)) * -(content_width - width));
		object.translation.y = ((bar_y.translation.y / (height - bar_width)) * -(content_height - height));
	}
}
