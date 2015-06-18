package net.merayen.merasynth.ui.objects.client;

import java.awt.Color;
import java.awt.Graphics2D;

import net.merayen.merasynth.ui.objects.components.CircularSlider;
import net.merayen.merasynth.ui.objects.node.Node;
import net.merayen.merasynth.ui.objects.node.Port;

public class PulseGenerator extends Node {

	private enum mode {
		SINE,
		TRIANGLE,
		SQUARE
	}
	
	private Port input_frequency; // Frequency
	
	// UI
	private CircularSlider frequency_slider;
	
	public void onInit() {
		super.onInit();
		input_frequency = new Port();
		input_frequency.translation.x = 0;
		input_frequency.translation.y = 2f;
		input_frequency.title = "Input Hz";
		addPort(input_frequency);
		
		// UI
		frequency_slider = new CircularSlider();
		add(frequency_slider);
		frequency_slider.translation.x = 5f;
		frequency_slider.translation.y = 5f;
	}
	
	public void onDraw(Graphics2D g) {
		super.onDraw(g);
		
		// Test middle point
		g.setColor(new Color(0,0,0));
		draw.fillOval(4.95f, 4.95f, 0.1f, 0.1f);
	}
}
