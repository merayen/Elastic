package net.merayen.elastic.uinodes.list.mix_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.components.InputSignalKnob;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	private InputSignalKnob fac_knob;

	@Override
	protected void onInit() {
		super.onInit();
		width = 100;
		height = 100;

		fac_knob = new InputSignalKnob();
		fac_knob.translation.x = 10;
		fac_knob.translation.y = 60;
		fac_knob.size = 30;
		add(fac_knob);
	}

	@Override
	protected void onCreatePort(UIPort port) {
		if(port.name.equals("a")) {
			port.translation.y = 20;
		}

		if(port.name.equals("b")) {
			port.translation.y = 40;
		}

		if(port.name.equals("fac")) {
			port.translation.y = 75;
		}

		if(port.name.equals("out")) {
			port.translation.x = 100;
			port.translation.y = 20;
		}
	}

	@Override
	protected void onRemovePort(UIPort port) {}

	@Override
	protected void onMessage(NodeParameterMessage message) {}

	@Override
	protected void onData(NodeDataMessage message) {}

	@Override
	protected void onParameter(String key, Object value) {}
}
