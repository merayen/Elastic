package net.merayen.elastic.ui.util;

import net.merayen.elastic.ui.TranslationData;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.MouseEvent;
import net.merayen.elastic.util.MutablePoint;

/*
 * Makes an UIObject moveable by clicking down and dragging it.
 * TODO Adapt better when scrolling while moving, as this currently offsets the dragging object quite a lot.
 *
 */
public class Movable extends MouseHandler { // TODO make it not inherit, rather contain MouseHandler
	public interface IMoveable {
		void onGrab();
		void onMove();
		void onDrop();
	}

	/**
	 * Slow down or speed up movement by setting this
	 */
	public float drag_scale_x = 1f;
	public float drag_scale_y = 1f;

	private MutablePoint start_absolute_position;
	private MutablePoint start_relative_position;
	private IMoveable handler;

	public Movable(UIObject movable, UIObject trigger) {
		this(movable, trigger, null);
	}

	public Movable(UIObject movable, UIObject trigger, MouseEvent.Button button) {
		/*
		 * Moving is triggered by the trigger, which moves the movable.
		 * The movable is usually the parent to the trigger.
		 */
		super(trigger, button);

		super.setHandler(new MouseHandler.Handler() {
			@Override
			public void onGlobalMouseUp(MutablePoint position) {
				if(start_absolute_position != null) {
					if(handler != null)
						handler.onDrop();

					start_absolute_position = null;
				}
			}

			@Override
			public void onGlobalMouseMove(MutablePoint global_position) {
				if(start_absolute_position != null && movable.isInitialized()) {
					TranslationData td = movable.getAbsoluteTranslation();
					movable.getTranslation().x = start_relative_position.getX() + (global_position.getX() - start_absolute_position.getX()) * td.scaleX / movable.getTranslation().scaleX * drag_scale_x;
					movable.getTranslation().y = start_relative_position.getY() + (global_position.getY() - start_absolute_position.getY()) * td.scaleY / movable.getTranslation().scaleY * drag_scale_y;

					if(handler != null)
						handler.onMove();
				}
			}

			@Override
			public void onMouseDown(MutablePoint position) {
				start_absolute_position = trigger.getAbsolutePosition(position.getX(), position.getY());
				start_relative_position = new MutablePoint(movable.getTranslation().x, movable.getTranslation().y);

				if(handler != null)
					handler.onGrab();
			}
		});
	}

	public void setHandler(IMoveable h) {
		handler = h;
	}

	@Override
	public void setHandler(MouseHandler.Handler lol) { // Should not access inherited setHandler()
		throw new RuntimeException();
	}

	public boolean isDragging() {
		return start_absolute_position != null;
	}
}
