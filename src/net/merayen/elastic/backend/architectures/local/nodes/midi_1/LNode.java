package net.merayen.elastic.backend.architectures.local.nodes.midi_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;

public class LNode extends LocalNode {

	final List<short[]> midi_from_ui = new ArrayList<>();

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onProcess(Map<String, Object> data) {
		if(data.containsKey("midi"))
			for(short[] m : (short[][])data.get("midi"))
				midi_from_ui.add(m);
	}

	@Override
	protected void onParameter(String key, Object value) {}

	@Override
	protected void onFinishFrame() {
		midi_from_ui.clear();
	}

	@Override
	protected void onDestroy() {}

}
