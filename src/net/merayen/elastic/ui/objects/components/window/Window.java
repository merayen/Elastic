package net.merayen.elastic.ui.objects.components.window;

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

	protected void onInit() {
		content.translation.x = 1f;
		content.translation.y = 11f;
		add(content);
	}

	protected void onDraw() {
		draw.setColor(80, 80, 80);
		draw.fillRect(0, 0, width, height);

		draw.setColor(120, 120, 120);
		draw.fillRect(1f, 1f, width - 2f, 9f);

		draw.setFont("Geneva", 2f);
		draw.setColor(200, 200, 200);
		draw.text(title, 5f, 2f);

		content.width = width - 2f;
		content.height = height - 12f;

		super.onDraw();
	}

	public UIObject getContentPane() {
		return content;
	}

	public void center(float parent_width, float parent_height) {
		translation.x = parent_width / 2 - width / 2;
		translation.y = parent_height / 2 - height / 2;
	}
}
