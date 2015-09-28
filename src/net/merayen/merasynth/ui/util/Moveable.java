package net.merayen.merasynth.ui.util;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.TranslationData;
import net.merayen.merasynth.ui.objects.UIObject;

public class Moveable extends MouseHandler {

	public interface IMoveable {
		public void onGrab(); // Is grabbed
		public void onMove(); // Is moving
		public void onDrop(); // Dropped
	}

	private Point original_absolute_position;
	private Point original_relative_position;
	private Point original_relative_position_local;
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
			public void onGlobalMouseUp(Point position) {
				if(original_absolute_position != null) {
					handler_class.onDrop();

					handler_class.onDrop();

					original_absolute_position = null;
				}
			}

			@Override
			public void onGlobalMouseMove(Point global_position) {
				if(original_absolute_position != null) {
					Point relative = moveable.getRelativePointFromAbsolute(global_position.x, global_position.y);
					TranslationData td = moveable.absolute_translation;
					moveable.translation.x = original_relative_position.x + (global_position.x - original_absolute_position.x) * td.scale_x - original_relative_position_local.x;
					moveable.translation.y = original_relative_position.y + (global_position.y - original_absolute_position.y) * td.scale_y - original_relative_position_local.y;
					handler_class.onMove();
				}
			}

			@Override
			public void onMouseDown(Point position) {
				//original_position = new Point(p.x + position.x - moveable.translation.x, p.y + position.y - moveable.translation.y);
				original_absolute_position = new Point(moveable.getAbsolutePosition());
				original_relative_position = new Point(moveable.translation.x, moveable.translation.y);
				original_relative_position_local = new Point(position.x, position.y);
				handler_class.onGrab();
			}
		});
	}
}
