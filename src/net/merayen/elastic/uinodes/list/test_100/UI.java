package net.merayen.elastic.uinodes.list.test_100;

import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	public UI() {
		super();
		width = 100;
		height = 100;
	}

	@Override
	protected void onCreatePort(UIPort port) {
		if(port.name.equals("input")) {
			port.translation.y = 20;
		}
	}

	@Override
	protected void onRemovePort(UIPort port) {
		
	}

	@Override
	protected void onMessage(NodeParameterMessage message) {}
}
