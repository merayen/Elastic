package net.merayen.elastic.backend.architectures.local.nodes.delay_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.util.Postmaster;

public class LProcessor extends LocalProcessor {
	private float audio[];

	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		AudioInlet input = (AudioInlet)getInlet("input");
		AudioOutlet output = (AudioOutlet)getOutlet("output");

		ensureBuffer(input);

		if(output != null) {
			if(input != null) {
				// TODO do things
				output.written = input.outlet.written;
				input.read = output.written;
			}
		}
	}

	private void ensureBuffer(AudioInlet input) {
		if(audio == null || audio.length != input.outlet.getChannelCount()) {

		}
	}

	@Override
	protected void onMessage(Postmaster.Message message) {

	}

	@Override
	protected void onDestroy() {

	}
}
