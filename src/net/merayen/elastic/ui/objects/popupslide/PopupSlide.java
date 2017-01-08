package net.merayen.elastic.ui.objects.popupslide;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.Dimension;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.top.Top;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Point;

/**
 * An alternative way of selecting
 */
public class PopupSlide extends UIObject {
	private class Background extends UIObject {
		private MouseHandler mouse;

		@Override
		protected void onInit() {
			mouse = new MouseHandler(this);
			mouse.setHandler(new MouseHandler.Handler() {
				@Override
				public void onMouseDown(Point position) {
					closeMenu();
				}
			});
		}

		@Override
		protected void onDraw() {
			Top top = (Top)search.getTop();

			// Closes ourself if clicked outside
			draw.empty(0, 0, top.width, top.height);
		}

		@Override
		protected void onEvent(IEvent event) {
			mouse.handle(event);
		}
	}

	private class Menu extends UIObject {

		/*@Override
		protected void onDraw() {
			Top top = (Top)search.getTop();
			Dimension dimension = getItemMaxDimension();

			// Close ourself if clicking outside
		}*/

		@Override
		protected void onUpdate() {
			Top top = (Top)search.getTop();
			Dimension dimension = getItemMaxDimension();
			translation.x = top.width / 2- dimension.width / 2;
			translation.y = top.height / 2 - dimension.height / 2;

			placeItems();
		}
	}

	private final List<PopupSlideItem> stack = new ArrayList<>();

	private final Menu menu = new Menu();
	private final Background background = new Background();

	@Override
	protected void onInit() {
		add(background);
		add(menu);
	}

	public void openPopup(PopupSlideItem item) {
		stack.add(item);
		menu.add(item);
	}

	private Dimension getItemMaxDimension() {
		float width = 0, height = 0;

		for(PopupSlideItem x : stack) {
			width = Math.max(width, x.width);
			height = Math.max(height, x.height);
		}

		return new Dimension(width, height);
	}

	private void placeItems() {
		float offset = stack.size() * 20;
		for(PopupSlideItem x : stack) {
			x.translation.y = offset;
			offset += 20;
		}
	}

	private void closeMenu() {
		this.getParent().remove(this);
	}
}
