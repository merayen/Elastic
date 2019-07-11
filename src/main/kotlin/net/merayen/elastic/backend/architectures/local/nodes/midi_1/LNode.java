package net.merayen.elastic.backend.architectures.local.nodes.midi_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.data.eventdata.MidiData;
import net.merayen.elastic.backend.logicnodes.list.midi_1.MidiNodeInputFrameData;
import net.merayen.elastic.system.intercom.InputFrameData;

public class LNode extends LocalNode {

	MidiData inputMidi;

	public LNode() {
		super(LProcessor.class);
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {}

	@Override
	protected void onProcess(InputFrameData data) {
		MidiNodeInputFrameData input = (MidiNodeInputFrameData)data;
		inputMidi = input.getMidiDataMessage().getMidiData();
	}

	@Override
	protected void onParameter(String key, Object value) {}

	@Override
	protected void onFinishFrame() {
		inputMidi = null;
	}

	@Override
	protected void onDestroy() {}

}
