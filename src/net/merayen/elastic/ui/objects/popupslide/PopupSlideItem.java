package net.merayen.elastic.ui.objects.popupslide;

import net.merayen.elastic.ui.UIObject;

public abstract class PopupSlideItem extends UIObject {
	private static class ContentBase extends UIObject {
		public float width, height; // Calculated
	}

	public static class Title extends ContentBase {
		@Override
		protected void onDraw() {
			draw.setColor(50, 50, 50);
			draw.fillRect(0, 0, width, height);
		}

		protected void drawText(String text) {
			draw.setColor(255, 255, 255);
			draw.setFont("", 12);
			draw.text(text, 10, 15);
		}
	}

	public static class Content extends ContentBase {
		@Override
		protected void onDraw() {
			draw.setColor(100, 100, 100);
			draw.fillRect(0, 0, width, height);
		}
	}

	public float width = 100;
	public float height = 100;

	protected final Title title;
	protected final Content content;

	public PopupSlideItem(Title title, Content content) {
		this.title = title;
		this.content = content;
	}

	@Override
	protected void onInit() {
		add(title);
		add(content);

		title.translation.x = 4;
		title.translation.y = 4;
		content.translation.x = 4;
		content.translation.y = 28;
	}

	@Override
	protected final void onDraw() {
		draw.setColor(50, 50, 50);
		draw.rect(0, 0, width, height);

		draw.setStroke(1);
		draw.setColor(100, 100, 100);
		draw.rect(2, 2, width - 4, height - 4);
	}

	@Override
	protected void onUpdate() {
		title.width = width - 8;
		title.height = 20;
		content.width = width - 8;
		content.height = height - 20 - 12;
	}
}
