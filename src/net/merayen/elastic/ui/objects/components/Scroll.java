package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.UIClip;

public class Scroll extends UIObject {
	public float width = 100;
	public float height = 100;
	public float content_width = 200;
	public float content_height = 200;
	public float bar_width = 10;
	public final UIObject container = new UIObject();

	private final UIClip clip = new UIClip();

	@Override
	protected void onInit() {
		clip.add(container);
		add(clip);
	}

	@Override
	protected void onDraw() {
		draw.setColor(0, 0, 0);
		draw.setStroke(bar_width);
		draw.fillRect(0, height - bar_width, width, bar_width);
		draw.fillRect(width - bar_width, 0, bar_width, height);

		draw.setColor(255, 0, 255);
		if(content_width - width > 0) {
			float x = ((-container.translation.x) / (content_width - clip.width));
			draw.fillRect(x * (width - bar_width), height - bar_width, bar_width, bar_width);
		}

		if(content_height - height > 0) {
			float y = ((-container.translation.y) / (content_height - clip.height));
			draw.fillRect(width - bar_width, y * (height - bar_width), bar_width, bar_width);
		}
	}

	@Override
	protected void onUpdate() {
		clip.width = width - bar_width;
		clip.height = height - bar_width;
		container.translation.x = (float)(Math.sin(System.currentTimeMillis() / 300d) * 60) - 50;
		container.translation.y = (float)(Math.sin(System.currentTimeMillis() / 250d) * 60) - 50;

		if(container.translation.x > 0)
			container.translation.x = 0;

		if(container.translation.y > 0)
			container.translation.y = 0;

		if(container.translation.x < -(content_width - width))
			container.translation.x = -(content_width - width);

		if(container.translation.y < -(content_height - height))
			container.translation.y = -(content_height - height);
	}
}
