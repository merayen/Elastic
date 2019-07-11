package net.merayen.elastic.backend.architectures.local.nodes.mix_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
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
	protected void onParameter(String key, Object value) {
		if(key.equals("mix"))
			mix = ((Number)value).floatValue();
	}

	@Override
	protected void onFinishFrame() {}

	@Override
	protected void onDestroy() {}
}
