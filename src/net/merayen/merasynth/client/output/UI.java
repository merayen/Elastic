package net.merayen.merasynth.client.output;

import net.merayen.merasynth.ui.objects.components.CircularSlider;
import net.merayen.merasynth.ui.objects.node.UINode;
import net.merayen.merasynth.ui.objects.node.UIPort;
import net.merayen.merasynth.ui.objects.node.components.PortParameterSlider;

public class UI extends UINode {
	private UIPort input_port;

	public void onInit() {
		super.onInit();

		width = 10f;
		height = 10f;

		titlebar.title = "Output";
	}

	@Override
	public void onCreatePort(String name) {
		if(name.equals("input")) {
			input_port = new UIPort("input", false);
			input_port.translation.x = 0f;
			input_port.translation.y = 2f;
			addPort(input_port);
		}
	}

	@Override
	public void onRemovePort(String name) {
		// Never removes any port anyway, so not implemented
	}
}
