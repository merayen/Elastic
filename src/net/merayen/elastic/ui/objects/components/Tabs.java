package net.merayen.elastic.ui.objects.components;

import java.util.List;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.UIEvent;
import net.merayen.elastic.ui.event.MouseEvent.Button;
import net.merayen.elastic.ui.objects.components.autolayout.AutoLayout;
import net.merayen.elastic.ui.util.MouseHandler;
import net.merayen.elastic.util.Point;

public class Tabs extends AutoLayout {
	public interface Handler {
		void onSelect(Tab tab);
	}

	public static class Tab extends UIObject {
		private interface Handler {
			void onClick();
		}

		private MouseHandler mouse_handler;
		private Handler handler;
		boolean active;
		float width;

		@Override
		public void onInit() {
			super.onInit();
			mouse_handler = new MouseHandler(this, Button.LEFT);
			mouse_handler.setHandler(new MouseHandler.Handler() {
				@Override
				public void onMouseClick(Point position) {
					handler.onClick();
				}
			});
		}

		@Override
		public void onEvent(UIEvent event) {
			mouse_handler.handle(event);
		}
	}

	public static class TextButton extends Tab {
		private float font_size;
		private String text;
		private float margin = 2;
		private boolean dirty;

		@Override
		public void onDraw(Draw draw) {
			super.onDraw(draw);
			if(text != null) {
				if(active) {
					draw.setColor(200, 200, 200);
					draw.fillRect(0, 0, width + margin * 2, font_size + margin * 2);
				}

				draw.setColor(0, 0, 0);
				draw.setFont("", font_size);
				if(dirty) {
					width = draw.getTextWidth(text);
					dirty = false;
				}
				draw.text(text, 0 + margin, font_size + margin);
				draw.rect(0, 0, width + margin * 2, font_size + margin * 2);
			}
		}

		public void setText(String text, float font_size) {
			this.text = text;
			this.font_size = font_size;
			dirty = true;
		}

		public void setMargin(float size) {
			margin = size;
			dirty = true;
		}
	}

	private final Handler handler;
	private int selected = -1;

	public Tabs(AutoLayout.Placement placement, Handler handler) {
		super(placement);
		this.handler = handler;
	}

	@Override
	public void add(UIObject element, int index) {
		if(!(element instanceof Tab))
			throw new RuntimeException("element must be a Tab-instance");

		super.add(element, index);
		((Tab)element).handler = () -> select(index);
	}

	public void select(int index) {
		if(index == selected)
			return;

		int i = 0;
		List<UIObject> tabs = getSearch().getChildren();

		for(UIObject o : tabs)
			((Tab)o).active = (i++ == index);

		handler.onSelect((Tab)tabs.get(index));

		selected = index;
	}
}
