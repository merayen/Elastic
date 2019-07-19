package net.merayen.elastic.backend.architectures.local.nodes.midi_1;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.data.eventdata.MidiData;
import net.merayen.elastic.backend.logicnodes.list.midi_1.MidiNodeInputFrameData;
import net.merayen.elastic.backend.nodes.BaseNodeData;
import net.merayen.elastic.system.intercom.InputFrameData;

public class LNode extends LocalNode {
	short[][] inputMidi;

	MidiData midiData;

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
		if (input.getMidiDataMessage() != null)
			midiData = input.getMidiDataMessage().getMidiData();

		if (input.getTemporaryMidi() != null)
			inputMidi = input.getTemporaryMidi();
	}

	@Override
	protected void onParameter(BaseNodeData instance) {}

	@Override
	protected void onFinishFrame() {
		inputMidi = null;
	}

	@Override
	protected void onDestroy() {}

}
