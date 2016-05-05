package net.merayen.elastic.client.mix;

import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;
import net.merayen.elastic.ui.objects.node.components.PortParameterSlider;

public class UI extends UINode {
	UIPort input_a_port;
	UIPort input_b_port;
	UIPort output_port;
	float mix_value;

	PortParameterSlider mix_slider;

	public void onInit() {
		super.onInit();

		width = 100f;
		height = 80f;

		titlebar.title = "Mix";
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("input_a")) {
			input_a_port = new UIPort("input_a", false);
			input_a_port.translation.x = 0f;
			input_a_port.translation.y = 20f;
			addPort(input_a_port);
		}

		if(name.equals("input_b")) {
			input_b_port = new UIPort("input_b", false);
			input_b_port.translation.x = 0f;
			input_b_port.translation.y = 40f;
			addPort(input_b_port);
		}

		if(name.equals("output")) {
			output_port = new UIPort("output", false);
			output_port.translation.x = width;
			output_port.translation.y = 30f;
			addPort(output_port);
		}

		if(name.equals("fac")) {
			mix_slider = new PortParameterSlider("fac");
			mix_slider.translation.x = 0f;
			mix_slider.translation.y = 55f;
			add(mix_slider);
			Glue glue_node = (Glue)this.getGlueNode();
			mix_slider.setHandler(new PortParameterSlider.IHandler() {

				@Override
				public void onChange(double value, boolean programatic) {
					mix_value = Math.round(value * 200) / 200f;
					mix_slider.setLabel(String.format("%d %%", (int)Math.round((mix_value*200 - 100))));
					glue_node.setFac(mix_value);
				}

				@Override
				public void onButton(int offset) {
					mix_value += 1 / 200f;
					mix_slider.setValue(mix_value);
				}
			});

			mix_slider.setValue(0.5);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}
}
