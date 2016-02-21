package net.merayen.merasynth.client.signalgenerator;

import net.merayen.merasynth.ui.objects.UIGroup;
import net.merayen.merasynth.ui.objects.components.BlockSelect;

public class WaveSelect extends UIGroup {

	private BlockSelect block_select;

	@Override
	protected void onInit() {
		super.onInit();

		block_select = new BlockSelect();
		add(block_select);
	}
}
