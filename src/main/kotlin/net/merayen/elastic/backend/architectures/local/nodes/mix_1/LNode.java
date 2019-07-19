package net.merayen.elastic.backend.architectures.local.nodes.mix_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.logicnodes.list.mix_1.Data;
import net.merayen.elastic.backend.nodes.BaseNodeData;
import net.merayen.elastic.system.intercom.InputFrameData;

public class LNode extends LocalNode {
	float mix;
	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onProcess(InputFrameData data) {}

	@Override
	protected void onParameter(BaseNodeData instance) {
		Data data = (Data)instance;
		Float mixData = data.getMix();

		if(mixData != null)
			mix = mixData;
	}

	@Override
	protected void onFinishFrame() {}

	@Override
	protected void onDestroy() {}
}
