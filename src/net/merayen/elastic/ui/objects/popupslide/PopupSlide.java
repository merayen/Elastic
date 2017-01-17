package net.merayen.elastic.ui.objects.popupslide;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.ui.Dimension;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.objects.top.Window;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.ui.util.UINodeUtil;
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
					popPopup();
				}
			});
		}

		@Override
		protected void onDraw() {
			Window window = UINodeUtil.getWindow(this);

			// Closes ourself if clicked outside
			draw.empty(0, 0, window.width, window.height);
		}

		@Override
		protected void onEvent(IEvent event) {
			mouse.handle(event);
		}
	}

	private class Menu extends UIObject {
		@Override
		protected void onUpdate() {
			Window window = UINodeUtil.getWindow(this);
			Dimension dimension = getItemMaxDimension();
			translation.x = window.width / 2- dimension.width / 2;
			translation.y = window.height / 2 - dimension.height / 2;

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
		float offset = 0;
		for(PopupSlideItem x : stack) {
			x.translation.y = offset;
			offset += 25;
			x.makeActive(false);
		}

		if(stack.size() > 0)
			stack.get(stack.size() - 1).makeActive(true);
	}

	private void popPopup() {
		menu.remove(stack.get(stack.size() - 1));

		placeItems();

		stack.remove(stack.size() - 1);

		if(stack.size() == 0)
			closePopup();
	}

	public void closePopup() {
		getParent().remove(this);
	}
}
