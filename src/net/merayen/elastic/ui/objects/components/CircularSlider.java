package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.Point;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.event.IEvent;
import net.merayen.elastic.ui.util.MouseHandler;

public class CircularSlider extends UIObject {
	public float size = 30f;
	public float drag_scale = 1f;
	public float pointer_length = 1f;

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
				setValue(drag_value - offset.y / (size / drag_scale));
			}

			@Override
			public void onMouseDown(Point position) {
				drag_value = value;
			}
		});
	}

	@Override
	protected void onDraw() {
		draw.setStroke(size / 20);
		draw.setColor(30, 30, 30);
		draw.oval(0, 0, size, size);

		draw.setColor(30, 30, 30);
		drawLine(0, 0.8f);
		drawLine(1, 0.8f);

		draw.setColor(200, 200, 200);
		//draw.setStroke(size / 10);
		drawLine(value, 0.6f);
	}

	@Override
	protected void onEvent(IEvent e) {
		mousehandler.handle(e);
	}

	private void drawLine(float value, float length) {
		//value = Math.max(Math.min(value, 1f), 0f);
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
