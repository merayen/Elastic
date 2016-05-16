package net.merayen.elastic.ui.util;

import net.merayen.elastic.ui.Point;
import net.merayen.elastic.ui.TranslationData;
import net.merayen.elastic.ui.UIObject;

/*
 * Makes an UIObject moveable by clicking down and dragging it.
 * TODO Adapt better when scrolling while moving, as this currently offsets the dragging object quite a lot.
 *
 */
public class Movable extends MouseHandler {

	public interface IMoveable {
		public void onGrab(); // Is grabbed
		public void onMove(); // Is moving
		public void onDrop(); // Dropped
	}

	private Point start_absolute_position;
	private Point start_relative_position;
	private IMoveable handler;

	public Movable(UIObject movable, UIObject trigger) {
		/*
		 * Moving is triggered by the trigger, which moves the movable.
		 * The movable is usually the parent to the trigger.
		 */
		super(trigger);

		super.setHandler(new MouseHandler.Handler() {
			@Override
			public void onGlobalMouseUp(Point position) {
				if(start_absolute_position != null) {
					if(handler != null)
						handler.onDrop();

					start_absolute_position = null;
				}
			}

			@Override
			public void onGlobalMouseMove(Point global_position) {
				if(start_absolute_position != null) {
					TranslationData td = movable.absolute_translation;
					movable.translation.x = start_relative_position.x + (global_position.x - start_absolute_position.x) * td.scale_x / movable.translation.scale_x;
					movable.translation.y = start_relative_position.y + (global_position.y - start_absolute_position.y) * td.scale_y / movable.translation.scale_y;

					if(handler != null)
						handler.onMove();
				}
			}

			@Override
			public void onMouseDown(Point position) {
				start_absolute_position = trigger.getAbsolutePosition(position.x, position.y);
				start_relative_position = new Point(movable.translation.x, movable.translation.y);

				if(handler != null)
					handler.onGrab();
			}
		});
	}

	public void setHandler(IMoveable h) {
		handler = h;
	}
}
