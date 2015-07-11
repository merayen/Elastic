package net.merayen.merasynth.ui.objects.components;

import net.merayen.merasynth.ui.objects.UIObject;

public class Label extends UIObject {
	public String label = "";
	public float font_size = 1f;
	public String font_name = "Geneva";
	private float label_width;

	protected void onDraw() {
		draw.setFont(font_name, font_size);
		label_width = draw.getTextWidth(label);

		draw.setColor(50, 50, 50);		
		draw.text(label, 0, font_size);
		draw.setColor(200, 200, 200);		
		draw.text(label, 0, font_size);
		super.onDraw();
	}

	public float getLabelWidth() {
		return label_width;
	}
}
