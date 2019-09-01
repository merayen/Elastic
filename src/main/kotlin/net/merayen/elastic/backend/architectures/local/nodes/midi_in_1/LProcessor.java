package net.merayen.elastic.backend.architectures.local.nodes.midi_in_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.system.intercom.ElasticMessage;

import java.util.List;

public class LProcessor extends LocalProcessor {

	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		MidiOutlet outlet = (MidiOutlet)getOutlet("output");

		if(outlet != null && !outlet.satisfied()) {
			List<short[]>buffer = ((LNode)getLocalNode()).buffer;

			if(!buffer.isEmpty())
				outlet.putMidi(0, buffer.toArray(new short[buffer.size()][]));

			outlet.written = buffer_size;
			outlet.push();
		}
	}

	@Override
	protected void onMessage(ElasticMessage message) {}

	@Override
	protected void onDestroy() {}
}
