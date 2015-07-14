package net.merayen.merasynth.client.signalgenerator;

import net.merayen.merasynth.ui.objects.components.CircularSlider;
import net.merayen.merasynth.ui.objects.node.Node;
import net.merayen.merasynth.ui.objects.node.Port;
import net.merayen.merasynth.ui.objects.node.components.PortParameterSlider;

public class UI extends Node {
	private CircularSlider frequency_slider;
	private PortParameterSlider port_parameter_slider;
	private Port output_port;
	private float frequency = 440;

	public static String getNodeName() {
		return "Wave";
	}

	public static String getNodeDescription() {
		return "Generates audio waves.";
	}

	public void onInit() {
		super.onInit();

		width = 10f;
		height = 10f;

		titlebar.title = "Wave";

		frequency_slider = new CircularSlider();
		add(frequency_slider);
		frequency_slider.translation.x = 5f;
		frequency_slider.translation.y = 5f;

		System.out.println(this.getGlueNode());
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency;
		port_parameter_slider.setValue(frequency / 8000.0);
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("frequency")) {
			port_parameter_slider = new PortParameterSlider();
			port_parameter_slider.translation.y = 2f;
			add(port_parameter_slider);

			port_parameter_slider.setHandler(new PortParameterSlider.IHandler() {
				@Override
				public void onChange(double value) {
					frequency = Math.round(value * 8000.0 * 10) / 10;
					port_parameter_slider.setLabel(String.format("%.2f Hz", frequency));
				}

				@Override
				public void onButton(int offset) {
					frequency += offset*1;
					port_parameter_slider.setValue(frequency / 8000.0 );
				}
			});

			port_parameter_slider.setValue(0);
			port_parameter_slider.setStep(0.1f);
		}

		if(name.equals("output")) {
			output_port = new Port();
			output_port.translation.x = width;
			output_port.translation.y = 2f;
			addOutputPort("output", output_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}
}
