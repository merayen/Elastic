package net.merayen.elastic.backend.architectures.local.nodes.midi_spread_1;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.logicnodes.list.midi_spread_1.Data;
import net.merayen.elastic.backend.nodes.BaseNodeData;
import net.merayen.elastic.system.intercom.InputFrameData;

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
					processor.basePitch = (count == 1) ? 0 : (i++ / (count - 1f) * 2 - 1) * width;
			}

			/*for(LocalProcessor x : processors) {
				LProcessor processor = (LProcessor)x;
				if(processor.tangent_down != null && processor.tangent_down[1] == tangent) {
					System.out.print(processor.basePitch + "\t");
				}
			}
			System.out.println();*/
		}
	}

	@Override
	protected void onProcess(InputFrameData data) {}

	@Override
	protected void onParameter(BaseNodeData instance) {
		Data data = (Data)instance;
		Float widthData = data.getWidth();

		if(widthData != null) {
			width = widthData;
			updateVoices();
		}
	}

	@Override
	protected void onFinishFrame() {}

	@Override
	protected void onDestroy() {}
}