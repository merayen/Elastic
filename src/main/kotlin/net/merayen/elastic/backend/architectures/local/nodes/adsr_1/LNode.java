package net.merayen.elastic.backend.architectures.local.nodes.adsr_1;

import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;

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
	protected void onProcess(Map<String, Object> data) {}

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
