package net.merayen.elastic.backend.architectures.local.nodes.midi_1;

import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.util.Postmaster.Message;

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
			outlet.midi[0] = midi_from_ui.toArray(new short[midi_from_ui.size()][]);
			outlet.written = buffer_size;
			outlet.push();
		}
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}
}
