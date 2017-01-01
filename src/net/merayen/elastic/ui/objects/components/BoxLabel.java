package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.UIObject;

public class BoxLabel extends UIObject {
	public String text = "";
	private float calculated_width;

	public BoxLabel(String text) {
		this.text = text;
	}

	public BoxLabel() {}

	@Override
	protected void onDraw() {
		draw.setFont("", 5);
		calculated_width = draw.getTextWidth(text) + 10;

		draw.setColor(0, 0, 0);
		draw.text(text, 5, 7);
		draw.rect(0, 0, calculated_width, 10);
	}

	public float getWidth() {
		return calculated_width;
	}

	public float getHeight() {
		return 10;
	}
}
