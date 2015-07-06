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

	// UI
	private CircularSlider frequency_slider;
	private ParameterSlider parameter_freq;
	private PortParameterSlider port_parameter_slider;
	private int frequency = 440;

	public void onInit() {
		super.onInit();

		titlebar.title = "Wave";

		port_parameter_slider = new PortParameterSlider();
		add(port_parameter_slider);
		port_parameter_slider.translation.y = 2f;

		port_parameter_slider.setHandler(new PortParameterSlider.IHandler() {
			@Override
			public void onChange(double value) {
				frequency = Math.round((float)Math.pow(value, 2) * 19999 + 1);
				port_parameter_slider.setLabel(String.format("%d Hz", frequency));
			}

			@Override
			public void onButton(int offset) {
				frequency += offset*10;
				port_parameter_slider.setValue(Math.pow(frequency / 19999.0, 0.5) );
			}
		});

		port_parameter_slider.setValue(0);
		port_parameter_slider.setStep(0.1f);

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
