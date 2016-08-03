package net.merayen.elastic.uinodes.list.group_1;

import net.merayen.elastic.ui.objects.node.UINode;

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
	protected void onCreatePort(String name) {}

	@Override
	protected void onRemovePort(String name) {}
}
