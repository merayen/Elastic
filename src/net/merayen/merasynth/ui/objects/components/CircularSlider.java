package net.merayen.merasynth.ui.objects.components;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.objects.UIObject;
import net.merayen.merasynth.ui.util.MouseHandler;

public class CircularSlider extends UIObject {
	public float size = 30f;

	// In radian, min and max position
	public float min = (float) Math.PI*1.8f;
	public float max = (float) Math.PI*0.2f;

	private float value = 0;

	private MouseHandler mousehandler;
	private float drag_value;

	protected void onInit() {
		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.Handler() {
			@Override
			public void onMouseDrag(Point start_point, Point offset) {
				setValue(drag_value - offset.y / (size * 5));
			}

			@Override
			public void onMouseDown(Point position) {
				drag_value = value;
			}
		});
	}

	@Override
	protected void onDraw() {
		draw.setStroke(2);
		draw.setColor(200, 200, 200);
		draw.fillOval(0, 0, size, size);

		draw.setColor(100, 100, 100);
		draw.fillOval(1, 1, size - 2, size - 2);

		draw.setColor(150, 150, 150);
		drawLine(0, 0.5f);
		drawLine(1, 0.5f);

		draw.setColor(200, 200, 200);
		drawLine(value, 0);
	}

	@Override
	protected void onEvent(IEvent e) {
		mousehandler.handle(e);
	}

	private void drawLine(float value, float length) {
		//value = Math.max(Math.min(value, 1f), 0f);
		draw.setStroke(2);
		draw.line(
			size/2 + (float)Math.sin(min + value * (max-min)) * (size * length) / 2.1f,
			size/2 + (float)Math.cos(min + value * (max-min)) * (size * length) / 2.1f,
			size/2 + (float)Math.sin(min + value * (max-min)) * size / 2.3f,
			size/2 + (float)Math.cos(min + value * (max-min)) * size / 2.3f
		);
	}

	public void setValue(float value) {
		this.value = Math.min(Math.max(value, 0f), 1f);
	}

	public float getValue() {
		return value;
	}
}
