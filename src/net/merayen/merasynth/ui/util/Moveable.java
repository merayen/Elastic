package net.merayen.merasynth.ui.util;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.objects.UIObject;

public class Moveable extends MouseHandler {

	public interface IMoveable {
		public void onGrab(); // Is grabbed
		public void onMove(); // Is moving
		public void onDrop(); // Dropped
	}

	private Point original_position;
	private IMoveable handler_class;
	private UIObject moveable;

	public Moveable(UIObject moveable, UIObject trigger) {
		/*
		 * Moving is triggered by the trigger, which moves the moveable.
		 * The moveable is usually the parent to the trigger.
		 */
		super(trigger);
		this.moveable = moveable;
	}

	public void setHandler(IMoveable cls) {
		/*
		 * MUST be called
		 */
		this.handler_class = cls;

		super.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseUp(Point position) {
				if(original_position != null) {
					handler_class.onDrop();
					
					handler_class.onDrop();
					
					original_position = null;
				}
			}

			@Override
			public void onGlobalMouseMove(Point global_position) {
				if(original_position != null) {
					moveable.translation.x = global_position.x - original_position.x;
					moveable.translation.y = global_position.y - original_position.y;
					handler_class.onMove();
				}
			}

			@Override
			public void onMouseDown(Point position) {
				Point p = moveable.getAbsolutePosition();
				original_position = new Point(p.x + position.x - moveable.translation.x, p.y + position.y - moveable.translation.y);
				handler_class.onGrab();
			}
		});
	}
}
