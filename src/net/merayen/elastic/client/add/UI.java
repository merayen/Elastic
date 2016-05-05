package net.merayen.elastic.client.add;

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

		titlebar.title = "Add";
	}

	@Override
	public void onCreatePort(String port_name) {
		if(port_name.startsWith("input_")) {
			UIPort p = new UIPort(port_name, false);
			p.translation.x = 0f;
			p.translation.y = 20f;
			addPort(p);
		}

		if(port_name.startsWith("output")) {
			UIPort p = new UIPort(port_name, true);
			p.translation.x = 100f;
			p.translation.y = 20f;
			addPort(p);
		}
	}

	@Override
	public void onRemovePort(String port_name) {
		removePort(getPort(port_name));
	}
}
