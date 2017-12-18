package net.merayen.elastic.backend.architectures.local.nodes.delay_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;

import java.util.Map;

public class LNode extends LocalNode {
	int delay;

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
		if(key.equals("delay_time")) {
			delay = (int) (((Number) value).doubleValue() * sample_rate);
			System.out.println("New delay_time " + delay);
		}
	}

	@Override
	protected void onFinishFrame() {}

	@Override
	protected void onDestroy() {}
}
