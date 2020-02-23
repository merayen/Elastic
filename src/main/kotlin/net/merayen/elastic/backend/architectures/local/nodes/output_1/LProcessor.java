package net.merayen.elastic.backend.architectures.local.nodes.output_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;

/**
 * Processor for outputting audio.
 * Accumulates audio and then the Net-node will acquire data when needed.
 */
public class LProcessor extends LocalProcessor {
	int voice_id = -1; // Set by the LNode when voice is created
	int written;

	@Override
	protected void onProcess() {
		// TODO some protection against processing multiple times on one frame?
		Inlet inlet = getInlet("input");

		if(inlet instanceof AudioInlet) {
			AudioInlet ai = (AudioInlet)inlet;
			LNode lnode = (LNode)getLocalNode();

			int channel_count = ai.outlet.getChannelCount();

			if(lnode.output[voice_id] == null || lnode.output[voice_id].length != channel_count) // See if the channel count has changed. If yes, we clear our output and recreate the channel buffers
				lnode.output[voice_id] = new float[channel_count][];

			// Note: does not copy
			System.arraycopy(ai.outlet.audio, 0, lnode.output[voice_id], 0, channel_count);

			written += buffer_size;
		}
	}

	@Override
	protected void onInit() {}

	@Override
	protected void onPrepare() {
		written = 0;
	}

	@Override
	protected void onDestroy() {
		((LNode)getLocalNode()).output[voice_id] = null; // Clean our buffer
	}
}
