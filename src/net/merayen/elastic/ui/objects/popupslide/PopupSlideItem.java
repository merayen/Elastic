package net.merayen.elastic.ui.objects.popupslide;

import net.merayen.elastic.ui.UIObject;

public abstract class PopupSlideItem extends UIObject {
	private static class ContentBase extends UIObject {
		public float width, height; // Calculated
	}

	public static class Title extends ContentBase {
		public String text = "";

		@Override
		protected void onDraw() {
			draw.setColor(250, 250, 250);
			draw.fillRect(0, 0, width, height);

			draw.setColor(0, 0, 0);
			draw.setFont("", 12);
			draw.text(text, 10, 15);
		}
	}

	public static class Content extends ContentBase { // TODO remove() Content when not in foreground
		@Override
		protected void onDraw() {
			draw.setColor(100, 100, 100);
			draw.fillRect(0, 0, width, height);
		}
	}

	public float width = 100;
	public float height = 100;

	protected final Title title = new Title();
	protected final Content content;

	public PopupSlideItem(Content content) {
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
	protected void onUpdate() {
		title.width = width - 8;
		title.height = 20;
		content.width = width - 8;
		content.height = height - 20 - 12;
	}

	void makeActive(boolean yes) {
		if(yes && content.getParent() == null)
			add(content);
		else if(!yes && content.getParent() != null)
			remove(content);
	}
}
