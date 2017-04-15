package net.merayen.elastic.uinodes.list.group_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {

	@Override
	protected void onInit() {
		super.onInit();
		translation.x = 100;
		translation.y = 100;
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		draw.setColor(100, 100, 200);
		draw.fillRect(10, 10, 20, 20);
	}

	@Override
	protected void onCreatePort(UIPort port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onRemovePort(UIPort port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(NodeParameterMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onData(NodeDataMessage message) {
		// TODO Auto-generated method stub
		
	}
}
