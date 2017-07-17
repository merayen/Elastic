package net.merayen.elastic.backend.architectures.local.nodes.midi_in_1;

import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.util.Postmaster.Message;

public class LProcessor extends LocalProcessor {

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPrepare() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onProcess() {
		MidiOutlet outlet = (MidiOutlet)getOutlet("output");

		if(outlet != null && !outlet.satisfied()) {
			List<short[]>buffer = ((LNode)getLocalNode()).buffer;

			outlet.midi[0] = buffer.toArray(new short[buffer.size()][]);
			outlet.written = buffer_size;
			outlet.push();
		}
	}

	@Override
	protected void onMessage(Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

	}

}
