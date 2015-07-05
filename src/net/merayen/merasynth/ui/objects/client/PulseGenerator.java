package net.merayen.merasynth.ui.objects.client;

import net.merayen.merasynth.ui.objects.components.CircularSlider;
import net.merayen.merasynth.ui.objects.components.parameterslider.ParameterSlider;
import net.merayen.merasynth.ui.objects.node.Node;
import net.merayen.merasynth.ui.objects.node.Port;
import net.merayen.merasynth.ui.objects.node.PortParameterSlider;

public class PulseGenerator extends Node {

	private enum mode {
		SINE,
		TRIANGLE,
		SQUARE
	}

	private Port input_frequency; // Frequency

	// UI
	private CircularSlider frequency_slider;
	private ParameterSlider parameter_freq;
	private PortParameterSlider node_parameter_slider;

	public void onInit() {
		super.onInit();

		titlebar.title = "Wave";

		node_parameter_slider = new PortParameterSlider();
		add(node_parameter_slider);
		node_parameter_slider.translation.y = 2f;

		/*input_frequency = new Port();
		input_frequency.translation.x = 0;
		input_frequency.translation.y = 2f;
		input_frequency.title = "Input Hz";
		addPort(input_frequency);

		// UI
		parameter_freq = new ParameterSlider();
		add(parameter_freq);
		parameter_freq.translation.x = 2f;
		parameter_freq.translation.y = 2f;*/

		frequency_slider = new CircularSlider();
		add(frequency_slider);
		frequency_slider.translation.x = 5f;
		frequency_slider.translation.y = 5f;
	}
}
