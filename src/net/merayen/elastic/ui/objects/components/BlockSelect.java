package net.merayen.elastic.ui.objects.components;

import net.merayen.elastic.ui.UIObject;

public class BlockSelect extends UIObject {

	@Override
	protected void onInit() {
		super.onInit();
	}

	@Override
	protected void onDraw() {
		draw.setColor(0, 0, 0);
		draw.setStroke(5);
		draw.rect(0, 0, 30, 20);
		super.onDraw();
	}
}
