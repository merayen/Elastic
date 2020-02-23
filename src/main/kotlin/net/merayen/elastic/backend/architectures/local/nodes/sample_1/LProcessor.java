package net.merayen.elastic.backend.architectures.local.nodes.sample_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet;
import net.merayen.elastic.backend.architectures.local.lets.SignalInlet;

public class LProcessor extends LocalProcessor {
	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		Inlet control = getInlet("control");
		AudioOutlet out = (AudioOutlet) getOutlet("out");

		if (out != null) {
			if (control instanceof MidiInlet) {
				// Playback is controlled by midi
				processWithMidi((MidiInlet) control, out);
			} else if (control instanceof SignalInlet) {
				// Playback is controlled by a signal (mostly just the speed)
				// Mostly appropriate when run inside a poly-node, that controls when it plays back
				processWithSignal((SignalInlet) control, out);
			} else {
				processWithNothing(out);
			}
		}
	}

	private void processWithMidi(MidiInlet control, AudioOutlet out) {
		out.push();
	}

	private void processWithSignal(SignalInlet control, AudioOutlet out) {
		out.push();
	}

	/**
	 * Just plays the sample as it is.
	 */
	private void processWithNothing(AudioOutlet out) {
		out.push();
	}

	@Override
	protected void onDestroy() {}
}
