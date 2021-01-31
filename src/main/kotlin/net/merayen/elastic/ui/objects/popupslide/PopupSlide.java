package net.merayen.elastic.ui.objects.popupslide;

import net.merayen.elastic.ui.MutableDimension;
import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.objects.top.window.Window;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.ui.util.UINodeUtil;
import net.merayen.elastic.util.MutablePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * An alternative way of selecting
 */
public class PopupSlide extends UIObject {
	private class Background extends UIObject {
		private MouseHandler mouse;

		@Override
		public void onInit() {
			mouse = new MouseHandler(this);
			mouse.setHandler(new MouseHandler.Handler() {
				@Override
				public void onMouseDown(MutablePoint position) {
					popPopup();
				}
			});
		}

		@Override
		public void onDraw(Draw draw) {
			Window window = UINodeUtil.INSTANCE.getWindow(this);

			// Closes ourself if clicked outside
			draw.empty(0, 0, window.getLayoutWidth(), window.getLayoutHeight());
		}

		@Override
		public void onEvent(UIEvent event) {
			mouse.handle(event);
		}
	}

	private class Menu extends UIObject {
		@Override
		public void onUpdate() {
			Window window = UINodeUtil.INSTANCE.getWindow(this);
			MutableDimension dimension = getItemMaxDimension();

			getTranslation().x = window.getLayoutWidth() / 2- dimension.getWidth() / 2;
			getTranslation().y = window.getLayoutHeight() / 2 - dimension.getHeight() / 2;

			placeItems();
		}
	}

	private final List<PopupSlideItem> stack = new ArrayList<>();

	private final Menu menu = new Menu();
	private final Background background = new Background();

	@Override
	public void onInit() {
		add(background);
		add(menu);
	}

	public void openPopup(PopupSlideItem item) {
		stack.add(item);
		menu.add(item);
	}

	private MutableDimension getItemMaxDimension() {
		float width = 0, height = 0;

		for(PopupSlideItem x : stack) {
			width = Math.max(width, x.getWidth());
			height = Math.max(height, x.getHeight());
		}

		return new MutableDimension(width, height);
	}

	private void placeItems() {
		float offset = 0;
		for(PopupSlideItem x : stack) {
			x.getTranslation().y = offset;
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
