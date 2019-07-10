package net.merayen.elastic.backend.architectures.local.nodes.midi_1;

import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;

public class LProcessor extends LocalProcessor {
	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		MidiOutlet outlet = (MidiOutlet)getOutlet("out");
		if(outlet != null) {
			List<short[]> midi_from_ui = ((LNode)getLocalNode()).midi_from_ui;
			outlet.putMidi(0, midi_from_ui.toArray(new short[midi_from_ui.size()][])); // TODO shouldn't quantize
			outlet.written = buffer_size;
			outlet.push();
		}
	}

	@Override
	protected void onMessage(Object message) {}

	@Override
	protected void onDestroy() {}
}
