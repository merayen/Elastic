package net.merayen.merasynth.client.signalgenerator;

import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.objects.node.UIPort;
import net.merayen.merasynth.ui.objects.node.components.PortParameterSlider;

public class UI extends UINode {
	private PortParameterSlider port_parameter_slider;
	private UIPort output_port;
	private float frequency = 440;

	public static String getNodeName() {
		return "Wave";
	}

	public static String getNodeDescription() {
		return "Generates audio waves.";
	}

	public void onInit() {
		super.onInit();

		width = 100f;
		height = 50f;

		titlebar.title = "Wave";

		createFrequencyPort();
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency;
		port_parameter_slider.setValue(frequency / 8000.0);
	}

	@Override
	public void onCreatePort(String name) {
		UI self = this;
		if(name.equals("frequency")) {
			// We create this port in onInit() as it is always available anyway
		}

		if(name.equals("output")) {
			output_port = new UIPort("output", true);
			output_port.translation.x = width;
			output_port.translation.y = 20f;
			addPort(output_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}

	private void createFrequencyPort() {
		UI self = this;
		port_parameter_slider = new PortParameterSlider("frequency");
		port_parameter_slider.translation.y = 20f;
		add(port_parameter_slider);

		port_parameter_slider.setHandler(new PortParameterSlider.IHandler() {
			@Override
			public void onChange(double value) {
				frequency = Math.round(value * 8000.0 * 10) / 10 + 0.1f;
				port_parameter_slider.setLabel(String.format("%.2f Hz", frequency));
				((Glue)self.getGlueNode()).changeFrequency(frequency);
			}

			@Override
			public void onButton(int offset) {
				frequency += offset*1;
				port_parameter_slider.setValue(frequency / 8000.0);
			}
		});

		port_parameter_slider.setValue(0);
		port_parameter_slider.setStep(0.1f);
	}
}
