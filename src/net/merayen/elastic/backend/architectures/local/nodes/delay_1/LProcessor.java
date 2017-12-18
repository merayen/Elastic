package net.merayen.elastic.backend.architectures.local.nodes.delay_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.util.Postmaster;

public class LProcessor extends LocalProcessor {
	private float audio[][];
	private long read, written;

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
				ensureBuffer(input);
				LNode lnode = (LNode)getLocalNode();

				int a = available();
				output.setChannelCount(audio.length);

				int output_written = 0;
				for(int channel = 0; channel < audio.length; channel++) {
					output_written = 0;
					int r = input.read;
					for (int i = 0; i < a; i++)
						audio[channel][(int)((written + i) % audio[channel].length)] = input.outlet.audio[channel][r++];

					for (int i = 0; i < (written+a)-read - lnode.delay; i++) {
						output.audio[channel][output.written + i] = audio[channel][(int)((read + i) % (audio[channel].length))];
						output_written++;
					}
				}
				written += a;
				read += output_written;
				output.written += output_written;
				input.read += a;
			}
		}
	}

	private void ensureBuffer(AudioInlet input) {
		LNode lnode = (LNode)getLocalNode();
		if(audio == null || audio.length != input.outlet.getChannelCount() || (audio.length > 0 && audio[0].length != (lnode.delay + buffer_size * 2))) {
			audio = new float[input.outlet.getChannelCount()][lnode.delay + buffer_size * 2];

			written = lnode.delay + buffer_size;
			read = 0;

			System.out.printf("Resizing delay line: channels=%d, samples=%d\n", audio.length, audio.length > 0 ? audio[0].length : 0);
		}
	}

	@Override
	protected void onMessage(Postmaster.Message message) {

	}

	@Override
	protected void onDestroy() {

	}
}
