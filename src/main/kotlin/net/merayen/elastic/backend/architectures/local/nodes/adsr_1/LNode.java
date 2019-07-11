package net.merayen.elastic.backend.architectures.local.nodes.adsr_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.system.intercom.InputFrameData;

public class LNode extends LocalNode {
	float attack, decay, sustain, release;

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
		if(key.equals("attack"))
			attack = ((Number)value).floatValue();
		if(key.equals("decay"))
			decay = ((Number)value).floatValue();
		if(key.equals("sustain"))
			sustain = ((Number)value).floatValue();
		if(key.equals("release"))
			release = ((Number)value).floatValue();
	}

	@Override
	protected void onFinishFrame() {}

	@Override
	protected void onDestroy() {}
}
