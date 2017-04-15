package net.merayen.elastic.backend.architectures.local.nodes.midi_in_1;

import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;

public class LNode extends LocalNode {

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {
		//
	}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onProcess(Map<String, Object> data) {

	}

	@Override
	protected void onParameter(String key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFinishFrame() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

	}
}
