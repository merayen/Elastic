package net.merayen.merasynth.ui.objects.window;

import net.merayen.merasynth.ui.objects.Group;

public class Window extends Group {
	private static class Content extends Group {
		/*
		 * Main content where the calling class can draw.
		 */
		public float width;
		public float height;
	}

	public float width = 10f;
	public float height = 10f;
	public String title = "";
	private Content content;

	protected void onInit() {
		content = new Content();
		content.translation.x = 0.1f;
		content.translation.y = 1.1f;
		add(content);
	}

	protected void onDraw() {
		draw.setColor(80, 80, 80);
		draw.fillRect(0, 0, width, height);

		draw.setColor(120, 120, 120);
		draw.fillRect(0.1f, 0.1f, width - 0.2f, 0.9f);

		draw.setFont("Geneva", 1.2f);
		draw.setColor(200, 200, 200);
		draw.text(title, 0.5f, 0.2f);

		content.width = width - 0.2f;
		content.height = height - 1.2f;

		super.onDraw();
	}

	public Group getContentPane() {
		return content;
	}
}
