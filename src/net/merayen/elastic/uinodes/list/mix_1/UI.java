package net.merayen.elastic.uinodes.list.mix_1;

import net.merayen.elastic.system.intercom.NodeDataMessage;
import net.merayen.elastic.system.intercom.NodeParameterMessage;
import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {

	@Override
	protected void onInit() {
		width = 100;
		height = 100;
	}

	@Override
	protected void onCreatePort(UIPort port) {
		if(port.name.equals("a")) {
			port.translation.y = 20;
		}

		if(port.name.equals("b")) {
			port.translation.y = 40;
		}

		if(port.name.equals("out")) {
			port.translation.x = 100;
			port.translation.y = 20;
		}
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
