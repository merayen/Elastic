package net.merayen.merasynth.ui.objects.components.parameterslider;

import net.merayen.merasynth.ui.objects.UIObject;

public class Button extends UIObject {
	public String label;

	public float width = 5f;
	public float height = 1.5f;

	protected void onDraw() {
		draw.setColor(50, 50, 50);
		draw.fillRect(0, 0, width, height);

		draw.setColor(120, 120, 120);
		draw.fillRect(0.1f, 0.1f, width - 0.2f, height - 0.2f);

		draw.setColor(200, 200, 200);
		float text_width = draw.getTextWidth(label);
		draw.text(label, (float)(width/2 - text_width/2), 1f);

		super.onDraw();
	}
}
