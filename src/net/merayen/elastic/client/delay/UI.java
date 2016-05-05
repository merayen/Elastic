package net.merayen.elastic.client.delay;

import net.merayen.elastic.ui.objects.components.ParameterSlider;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;
import net.merayen.elastic.ui.objects.node.components.PortParameterSlider;

public class UI extends UINode {
	UIPort input_port;
	UIPort output_port;
	ParameterSlider delay_slider;

	float delay_value; // In seconds

	PortParameterSlider mix_slider;

	public void onInit() {
		super.onInit();
		UI self = this;

		width = 100f;
		height = 50f;

		titlebar.title = "Delay";

		delay_slider = new ParameterSlider();
		delay_slider.translation.x = 10f;
		delay_slider.translation.y = 20f;
		add(delay_slider);

		delay_slider.setHandler(new ParameterSlider.IHandler() {

			@Override
			public void onChange(double value, boolean programatic) {
				delay_value = Math.round(value * 20000) / 20000f;

				if(!programatic) // If not set by setValue, but by users himself
					((Glue)self.getGlueNode()).changeDelay(delay_value * 20 - 10);

				delay_slider.label = String.format("%.3f", delay_value*20 - 10);
			}

			@Override
			public void onButton(int offset) {
				delay_slider.setValue(delay_value + offset / 20.0);
			}
		});

		delay_slider.scale = 0.2f;
		delay_slider.setValue(0);
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("input")) {
			input_port = new UIPort("input", false);
			input_port.translation.x = 0f;
			input_port.translation.y = 25f;
			addPort(input_port);
		} else if(name.equals("output")) {
			output_port = new UIPort("output", false);
			output_port.translation.x = width;
			output_port.translation.y = 25f;
			addPort(output_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}

	public void setDelayValue(float value) {
		delay_value = value;
	}
}
