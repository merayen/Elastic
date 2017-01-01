package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.UIObject;

public class InputSignalKnob extends UIObject { // TODO delete
	public float size = 30;

	private CircularSlider amplitude;
	private CircularSlider offset;

	@Override
	protected void onInit() {
		amplitude = new CircularSlider();
		amplitude.setValue(0.5f);
		amplitude.size = size;
		amplitude.drag_scale = 0.05f;
		add(amplitude);

		offset = new CircularSlider();
		offset.translation.x = size / 4;
		offset.translation.y = size / 4;
		offset.size = size / 2;
		offset.drag_scale = 0.05f;
		offset.setValue(0.5f);
		add(offset);
	}

	@Override
	protected void onDraw() {
		super.onDraw();
		draw.setColor(0, 0, 0);

		String amplitude_text = getFormatted(getAmplitude(), 2);
		String offset_text = getFormatted(getOffset(), 2);

		draw.setColor(80, 80, 80);
		draw.fillOval(0, 0, size, size);

		draw.setColor(255, 255, 255);
		draw.setFont("", size / 6);
		draw.text(amplitude_text, size / 2 - draw.getTextWidth(amplitude_text) / 2, size * 0.2f);

		draw.setFont("", size / 7);
		draw.text(offset_text, size / 2 - draw.getTextWidth(offset_text) / 2, size * 0.5f);
	}

	public float getAmplitude() {
		return (float)Math.pow(amplitude.getValue() * 2, 8);
	}

	public float getOffset() {
		float v = offset.getValue();
		return (float)(Math.pow(Math.max(0.5f, v) * 2, 10) + -Math.pow(Math.max(0.5f, 1-v) * 2, 10));
	}

	private String getFormatted(float number, int max) {
		int decimals = Math.max(0, max - (int)Math.max(0, Math.log10(Math.abs(number))));
		return String.format("%." + decimals + "f", number);
	}
}
