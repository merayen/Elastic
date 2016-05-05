package net.merayen.elastic.uinodes.list.signalgenerator_100;

import net.merayen.elastic.ui.UIObject;
import net.merayen.elastic.ui.objects.components.BlockSelect;

public class WaveSelect extends UIObject {

	private BlockSelect block_select;

	@Override
	protected void onInit() {
		super.onInit();

		block_select = new BlockSelect();
		add(block_select);
	}
}
