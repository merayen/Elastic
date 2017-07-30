package net.merayen.elastic.backend.architectures.local.nodes.midi_spread_1;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;

public class LNode extends LocalNode {
	private float width = 0f;

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	void updateVoices() { // TODO detect sub-sessions
		List<LocalProcessor> processors = getProcessors();
		Set<Short> tangents = new HashSet<>();
		for(LocalProcessor x : processors) {
			LProcessor processor = (LProcessor)x;
			if(processor.tangent_down != null)
				tangents.add(processor.tangent_down[1]);
		}

		for(short tangent : tangents) {
			int count = 0;
			for(LocalProcessor x : processors) {
				LProcessor processor = (LProcessor)x;
				if(processor.tangent_down != null && processor.tangent_down[1] == tangent)
					count++;
			}

			int i = 0;
			for(LocalProcessor x : processors) {
				LProcessor processor = (LProcessor)x;
				if(processor.tangent_down != null && processor.tangent_down[1] == tangent)
					processor.pitch = (count > 1 ? ((i++ / ((float)count - 1)) * 2 - 1) * width : 0);
			}
		}
	}

	@Override
	protected void onProcess(Map<String, Object> data) {}

	@Override
	protected void onParameter(String key, Object value) {
		if(key.equals("width")) {
			width = ((Number)value).floatValue();
			updateVoices();
		}
	}

	@Override
	protected void onFinishFrame() {}

	@Override
	protected void onDestroy() {}
}
