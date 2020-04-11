package net.merayen.elastic.backend.architectures.local.nodes.delay_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;

public class LProcessor extends LocalProcessor {
	private Delay[] delays;

	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		AudioInlet input = (AudioInlet)getInlet("input");
		AudioOutlet output = (AudioOutlet)getOutlet("output");

		if(output != null) {
			if(input != null) {
				if(available()) {
					int channelCount = getLocalNode().getParentGroupNode().getChannelCount();

					ensureDelayBuffers(channelCount);

					for (int channel = 0; channel < channelCount; channel++) {
						int position = delays[channel].process(input.outlet.audio[channel], 0, buffer_size);
						for (int i = 0; i < buffer_size; i++)
							output.audio[channel][i] = delays[channel].buffer[position++ % delays[channel].buffer.length];
					}

					output.push();
				}
			} else {
				output.push();
			}
		}
	}

	@Override
	protected void onDestroy() {}

	private void ensureDelayBuffers(int channelCount) {
		if(delays == null || delays.length != channelCount) {
			delays = new Delay[channelCount];
			for(int i = 0; i < channelCount; i++) {
				delays[i] = new Delay(sampleRate * 4); // Allows 1 second of delay

				// DEBUG. Take these parameters from the UI instead
				final int TAPS = 100;
				for(int lol = 0; lol < TAPS; lol++)
					delays[i].addTap(new Delay.Tap((int)(Math.floor(sampleRate * Math.random() * 2)), 0.02f, 0.8f / TAPS));
					//delays[i].addTap(new Delay.Tap((int)(Math.floor(((lol + 1f) / ((float)TAPS + 1f)) * sample_rate * Math.random())), 0.5f, 0.1f / TAPS));

				delays[i].addTap(new Delay.Tap(sampleRate, 0.0f, .2f));
				delays[i].addTap(new Delay.Tap((int)(sampleRate * 2f), 0.0f, .3f));
			}
		}
	}
}
