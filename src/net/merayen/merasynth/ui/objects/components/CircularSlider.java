package net.merayen.merasynth.ui.objects.components;

import java.awt.Color;
import java.awt.Graphics2D;

import net.merayen.merasynth.ui.Point;
import net.merayen.merasynth.ui.event.IEvent;
import net.merayen.merasynth.ui.objects.UIObject;
import net.merayen.merasynth.ui.util.MouseHandler;

public class CircularSlider extends UIObject {
	public float size = 3f;
	
	// In radian, min and max position
	public float min = (float) Math.PI*1.8f;
	public float max = (float) Math.PI*0.2f;
	
	private float value = 0;
	
	private MouseHandler mousehandler;
	private float drag_value;

	protected void onCreate() {
		mousehandler = new MouseHandler(this);
		mousehandler.setHandler(new MouseHandler.IMouseHandler() {
			
			@Override
			public void onMouseUp(Point position) {}
			
			@Override
			public void onMouseOver() {}
			
			@Override
			public void onMouseOut() {}
			
			@Override
			public void onMouseMove(Point position) {}
			
			@Override
			public void onMouseDrop(Point start_point, Point offset) {}
			
			@Override
			public void onMouseDrag(Point start_point, Point offset) {
				setValue(drag_value - offset.y/10f);
			}
			
			@Override
			public void onMouseDown(Point position) {
				drag_value = value;
			}
			
			@Override
			public void onGlobalMouseMove(Point global_position) {}
		});
	}
	
	@Override
	protected void onDraw(Graphics2D g) {
		g.setColor(new Color(50, 50, 50));
		draw.fillOval(0, 0, size, size);
		g.setColor(new Color(200, 200, 200));
		draw.fillOval(0.1f, 0.1f, size - 0.2f, size - 0.2f);
		
		g.setColor(new Color(100, 100, 100));
		drawLine(value, 0);
		
		g.setColor(new Color(150, 150, 150));
		drawLine(0, 0.5f);
		drawLine(1, 0.5f);
	}
	
	@Override
	protected void onEvent(IEvent e) {
		mousehandler.handle(e);
	}
	
	private void drawLine(float value, float length) {
		//value = Math.max(Math.min(value, 1f), 0f);
		draw.setStroke(0.2f);
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
