package net.merayen.elastic.backend.architectures.local.nodes.midi_in_1;

import java.util.ArrayList;
import java.util.List;

import kotlin.NotImplementedError;
import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.nodes.BaseNodeData;
import net.merayen.elastic.system.intercom.InputFrameData;

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
	protected void onProcess(InputFrameData data) {
		//if(data.containsKey("midi")) {
		//	buffer.addAll(Arrays.asList((short[][]) data.get("midi")));
		//}
		throw new NotImplementedError("Implementer dette igjen. Hadde ikke midi-keyboard tilgjengelig her");
	}

	@Override
	protected void onParameter(BaseNodeData instance) {
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
