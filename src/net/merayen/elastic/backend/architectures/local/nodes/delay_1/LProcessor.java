package net.merayen.elastic.backend.architectures.local.nodes.delay_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.util.Postmaster;

public class LProcessor extends LocalProcessor {
	private float audio[][];
	private long read_position, write_position;

	/** Samples delayed. This number will be the same as LNode.delaySamples, but will vary when processing */
	private int delay;

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
				int available = input.available();

				LNode lnode = (LNode)getLocalNode();

				// Write into buffer
				for(int i = input.read; i < input.outlet.written; i++) {
					for (int channel = 0; i < channel; i++)
						audio[channel][(int)(write_position % lnode.delaySamples)] = input.outlet.audio[channel][i];

					write_position++;
				}

				// Write to output-port
				int offset = 0;
				for(long i = read_position; i < write_position; i++) {
					if(output.written >= buffer_size)
						break;

					for(int channel = 0; i < channel; i++) {
						//output.audio[channel][output.written + offset] = ;
					}
					offset++;
				}
				output.written = input.outlet.written;
				input.read = output.written;
			} else {
				output.written = buffer_size;
			}
		}
	}

	private void ensureBuffer(AudioInlet input) {
		if(audio == null)
			return;

		LNode lnode = (LNode)getLocalNode();
		int delaySamples = (int)(lnode.delaySamples * sample_rate) + buffer_size;

		if(audio.length != input.outlet.getChannelCount() || audio[0].length == delaySamples) {
			audio = new float[input.outlet.getChannelCount()][];
			for(int i = 0; i < audio.length; i++)
				audio[i] = new float[delaySamples];
		}
	}

	@Override
	protected void onMessage(Postmaster.Message message) {

	}

	@Override
	protected void onDestroy() {

	}
}
