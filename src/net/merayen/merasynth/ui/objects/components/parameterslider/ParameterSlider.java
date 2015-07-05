package net.merayen.merasynth.ui.objects.components.parameterslider;

import net.merayen.merasynth.ui.objects.Group;

public class ParameterSlider extends Group {
	float width = 8f;
	Button left_button, right_button;

	protected void onInit() {
		left_button = new Button();
		left_button.translation.x = 0f;
		left_button.translation.y = 0f;
		left_button.label = "-";
		left_button.width = 1.1f;
		add(left_button);

		right_button = new Button();
		right_button.translation.x = width - 1.1f;
		right_button.translation.y = 0f;
		right_button.label = "+";
		right_button.width = 1.1f;
		add(right_button);
	}

	protected void onDraw() {
		draw.setColor(100, 100, 100);
		draw.fillRect(1, 0, width - 2f, 1.5f);

		draw.setColor(150, 150, 150);
		draw.fillRect(1.1f, 0.1f, width - 2.1f, 1.3f);

		super.onDraw();
	}
}
