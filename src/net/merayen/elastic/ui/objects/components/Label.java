package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.UIObject;

public class Label extends UIObject {
	public enum Align {
		LEFT, CENTER, RIGHT
	}

	public String label = "";
	public float font_size = 10f;
	public String font_name = "Geneva";
	public Align align;
	private float label_width;

	protected void onDraw() {
		draw.setFont(font_name, font_size);
		label_width = draw.getTextWidth(label);

		float x_offset = 0;
		if(align == Align.CENTER)
			x_offset = -label_width / 2f;
		else if(align == Align.RIGHT)
			x_offset = -label_width;

		draw.setColor(50, 50, 50);
		draw.text(label, x_offset - font_size / 10f, font_size - font_size / 10f);
		draw.setColor(200, 200, 200);
		draw.text(label, x_offset, font_size);

		super.onDraw();
	}

	public float getLabelWidth() {
		return label_width;
	}
}
