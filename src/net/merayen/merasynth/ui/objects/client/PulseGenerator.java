package net.merayen.merasynth.ui.objects.client;

import java.awt.Graphics2D;

import net.merayen.merasynth.ui.objects.node.Node;
import net.merayen.merasynth.ui.objects.node.Port;

public class PulseGenerator extends Node {

	private enum mode {
		SINE,
		TRIANGLE,
		SQUARE
	}
	
	private Port input_frequency; // Frequency
	
	public void onCreate() {
		super.onCreate();
		input_frequency = new Port();
		input_frequency.translation.x = 0;
		input_frequency.translation.y = 0.02f;
		input_frequency.title = "Input Hz";
		addPort(input_frequency);
	}
	
	public void onDraw(Graphics2D g) {
		super.onDraw(g);
	}
}
