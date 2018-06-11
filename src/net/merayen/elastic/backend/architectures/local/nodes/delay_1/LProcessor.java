package net.merayen.elastic.backend.architectures.local.nodes.delay_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.util.Postmaster;

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
				int start = input.read;
				int stop = input.outlet.written;
				if(stop - start > 0) {

					int channelCount = input.outlet.getChannelCount();

					ensureDelayBuffers(channelCount);

					output.setChannelCount(channelCount);

					for (int channel = 0; channel < channelCount; channel++) {
						int position = delays[channel].process(input.outlet.audio[channel], start, stop);
						for (int i = start; i < stop; i++)
							output.audio[channel][i] = delays[channel].buffer[position++ % delays[channel].buffer.length];
					}

					output.written = stop;
					input.read = stop;
					output.push();
				}
			} else {
				output.written = buffer_size;
				output.push();
			}
		} else if(input != null) {
			input.read = input.outlet.written;
		}
	}

	@Override
	protected void onMessage(Postmaster.Message message) {}

	@Override
	protected void onDestroy() {}

	private void ensureDelayBuffers(int channelCount) {
		if(delays == null || delays.length != channelCount) {
			delays = new Delay[channelCount];
			for(int i = 0; i < channelCount; i++) {
				delays[i] = new Delay(sample_rate * 4); // Allows 1 second of delay

				// DEBUG. Take these parameters from the UI instead
				final int TAPS = 100;
				for(int lol = 0; lol < TAPS; lol++)
					delays[i].addTap(new Delay.Tap((int)(Math.floor(Math.random() * sample_rate * 2)), 0.5f, 0.99f / TAPS));

				/*delays[i].addTap(new Delay.Tap(sample_rate / 4, 0.0f, .2f));
				delays[i].addTap(new Delay.Tap(sample_rate / 2, 0.0f, .3f));*/
			}
		}
	}
}
