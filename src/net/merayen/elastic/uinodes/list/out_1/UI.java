package net.merayen.elastic.uinodes.list.out_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {
	@Override
	protected void onInit() {
		super.onInit();
		width = 100;
		height = 40;
		titlebar.title = "Point";
	}

	@Override
	protected void onCreatePort(UIPort port) {
		port.translation.y = 20;
	}

	@Override
	protected void onRemovePort(UIPort port) {}

	@Override
	protected void onMessage(NodeParameterMessage message) {}

	@Override
	protected void onData(NodeDataMessage message) {}
}
