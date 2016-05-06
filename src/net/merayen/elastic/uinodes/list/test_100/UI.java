package net.merayen.elastic.uinodes.list.test_100;

import net.merayen.elastic.ui.objects.node.UINode;
import net.merayen.elastic.ui.objects.node.UIPort;

public class UI extends UINode {

	@Override
	protected void onInit() {
		super.onInit();
		width = 100;
		height = 100;

		UIPort port = new UIPort(node_id, true);
		addPort(port);
	}

	@Override
	protected void onDraw() {
		super.onDraw();

		
	}

	@Override
	protected void onCreatePort(String name) {
		
	}

	@Override
	protected void onRemovePort(String name) {
		
	}

}
