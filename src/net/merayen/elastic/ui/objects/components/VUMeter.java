package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.UIObject;

public class VUMeter extends UIObject { // TODO break up into smaller classes for better draw order?
	private class Panel extends UIObject {
		public float c_radius;

		@Override
		public void onDraw(Draw draw) {
			if(panel_handler != null)
				panel_handler.draw(draw, c_radius, Math.PI / 2f + Math.PI / 4f, Math.PI / 2f);
		}
	}

	public static abstract class PanelHandler {
		public abstract void draw(Draw draw, float c_radius, double start_rad, double length_rad);
	}

	private float value;
	private Panel panel = new Panel();
	private PanelHandler panel_handler;
	public float width = 8;
	public float height = 5;
	public String label = "VU";

	public void onInit() {
		add(panel);
	}

	@Override
	public void onDraw(Draw draw) {
		drawHouse(draw);
		drawBackground(draw);
		drawLine(draw);
		drawOuterHouse(draw);
		updatePanel();
	}

	private void drawHouse(Draw draw) {
		draw.setColor(150, 150, 150);
		draw.fillRect(0, 0, width, height);

		draw.setColor(200, 190, 180);
		draw.fillRect(0.1f, 0.1f, width - 0.2f, height - 0.2f);
	}

	private void drawBackground(Draw draw) {
		draw.setColor(100, 100, 100);
		draw.setFont("Arial", 0.7f);
		draw.text(label, width / 2f - draw.getTextWidth(label) / 2f, 3f);
	}

	private void drawLine(Draw draw) {
		double n = -Math.PI / 2f - Math.PI / 4F - value * (Math.PI / 2f);
		float c_radius = Math.min(width, height);
		float x = width / 2f + (float)Math.sin(n) * c_radius * 0.8f;
		float y = height + (float)Math.cos(n) * c_radius * 0.8f;

		draw.setColor(50, 50, 50);
		draw.setStroke(.1f);
		draw.line(width / 2f, height * 0.9f, x, y);
	}

	private void drawOuterHouse(Draw draw) {
		draw.setColor(70, 70, 70);
		draw.fillRect(0.1f, height * 0.7f, width - 0.2f, height * 0.3f - 0.1f);

		// Funky stripes
		draw.setColor(80, 80, 80);
		for(float i = height * 0.72f; i < height; i += height * 0.05f)
			draw.line(0.2f, i, width - 0.2f, i);
	}

	private void updatePanel() {
		panel.c_radius = Math.min(width, height) * 0.8f;
		panel.getTranslation().x = width / 2f;
		panel.getTranslation().y = height;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public void setBackgroundFunction() {
		// TODO a custom function that draws the background
	}

	public void setPanelDrawFunc(PanelHandler panel_handler) {
		this.panel_handler = panel_handler;
	}
}
