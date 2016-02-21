package net.merayen.merasynth.ui.objects.components;

import net.merayen.merasynth.ui.objects.UIGroup;

public class BlockSelect extends UIGroup {

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
