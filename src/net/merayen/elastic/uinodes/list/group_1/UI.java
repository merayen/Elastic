package net.merayen.elastic.uinodes.list.group_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.Draw;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {

	@Override
	public void onInit() {
		super.onInit();
	}

	@Override
	public void onDraw(Draw draw) {
		super.onDraw(draw);
	}

	@Override
	protected void onCreatePort(UIPort port) {}

	@Override
	protected void onRemovePort(UIPort port) {}

	@Override
	protected void onMessage(NodeParameterMessage message) {}

	@Override
	protected void onData(NodeDataMessage message) {}

	@Override
	protected void onParameter(String key, Object value) {}
}
