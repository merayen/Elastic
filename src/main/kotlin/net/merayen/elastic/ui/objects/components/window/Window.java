package net.merayen.elastic.ui.objects.components.window;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;

public class Window extends UIObject {
	private static class Content extends UIObject {
		/*
		 * Main content where the calling class can draw.
		 */
		public float width;
		public float height;
	}

	public float width = 100f;
	public float height = 100f;
	public String title = "";
	private Content content = new Content();

	public void onInit() {
		content.getTranslation().x = 1f;
		content.getTranslation().y = 11f;
		add(content);
	}

	public void onDraw(Draw draw) {
		draw.setColor(80, 80, 80);
		draw.fillRect(0, 0, width, height);

		draw.setColor(120, 120, 120);
		draw.fillRect(1f, 1f, width - 2f, 9f);

		draw.setFont("Geneva", 2f);
		draw.setColor(200, 200, 200);
		draw.text(title, 5f, 2f);

		content.width = width - 2f;
		content.height = height - 12f;

		super.onDraw(draw);
	}

	public UIObject getContentPane() {
		return content;
	}

	public void center(float parent_width, float parent_height) {
		getTranslation().x = parent_width / 2 - width / 2;
		getTranslation().y = parent_height / 2 - height / 2;
	}
}
