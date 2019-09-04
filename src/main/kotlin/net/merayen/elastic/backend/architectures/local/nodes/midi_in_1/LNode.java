package net.merayen.elastic.backend.architectures.local.nodes.midi_in_1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.logicnodes.list.midi_in_1.MidiIn1InputFrameData;
import net.merayen.elastic.backend.nodes.BaseNodeProperties;
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
	protected void onSpawnProcessor(LocalProcessor lp) {
	}

	@Override
	protected void onProcess(InputFrameData data) {
		MidiIn1InputFrameData input = (MidiIn1InputFrameData) data;
		if (input.getMidi() != null)
			buffer.addAll(Arrays.asList((short[][]) input.getMidi()));
	}

	@Override
	protected void onParameter(BaseNodeProperties instance) {
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
