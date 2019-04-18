package net.merayen.elastic.backend.architectures.local.nodes.midi_in_1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;

public class LNode extends LocalNode {
	List<short[]> buffer = new ArrayList<>();

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {
		//
	}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onProcess(Map<String, Object> data) {
		if(data.containsKey("midi")) {
			buffer.addAll(Arrays.asList((short[][]) data.get("midi")));
		}
	}

	@Override
	protected void onParameter(String key, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFinishFrame() {
		buffer.clear();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

	}
}
