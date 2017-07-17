package net.merayen.elastic.backend.architectures.local.nodes.output_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;

public class LNode extends LocalNode {
	public LNode() {
		super(LProcessor.class);
	}

	// TODO null-ify channels that gets removed
	final float[/* voice id */][/* channel no */][/* sample index */] output = new float[256][][]; // LProcessors writes to this directly, using their LProcessor.voice_id to figure out the index
	float[] offset = new float[256];

	@Override
	protected void onInit() {}

	@Override
	protected void onProcess(Map<String, Object> data) {}

	@Override
	protected void onParameter(String key, Object value) {}

	@Override
	protected void onDestroy() {}

	@Override
	protected void onSpawnProcessor(LocalProcessor lp) {
		List<Integer> channels = new ArrayList<>();

		for(LocalProcessor localprocessor : getProcessors())
			channels.add(((LProcessor)localprocessor).voice_id);

		for(int channel_id = 0; channel_id < 256; channel_id++) {
			if(!channels.contains(channel_id)) {
				((LProcessor)lp).voice_id = channel_id;
				break;
			}
		}
	}

	@Override
	protected void onFinishFrame() {
		int channel_count = countChannels();
		float[][] channels = new float[channel_count][];

		for(int i = 0; i < channel_count; i++) // Creating new outgoing buffers to separate the processor and external receivers of this data
			channels[i] = new float[buffer_size];

		float[] amplitude = new float[channel_count];

		for(int i = 0; i < channel_count; i++)
			offset[i] /= (1 + (buffer_size / (float)sample_rate));

		for(int voice_no = 0; voice_no < output.length; voice_no++) {
			if(output[voice_no] != null) {
				for(int channel_no = 0; channel_no < channel_count; channel_no++) {
	
					float[] in = output[voice_no][channel_no];
					float[] out = channels[channel_no];

					for(int i = 0; i < buffer_size; i++)
						out[i] += in[i];
	
					// Measure max amplitude
					for(float v : output[voice_no][channel_no]) {
						if(Math.abs(amplitude[channel_no]) < v)
							amplitude[channel_no] = Math.abs(v);

						offset[channel_no] += v;
					}
				}
			}
		}

		outgoing.put("audio", channels);

		if(channel_count > 0) {
			outgoing.put("vu", amplitude);
			outgoing.put("offset", offset);
		}
	}

	private int countChannels() {
		int count = 0;
		for(int i = 0; i < output.length; i++) {
			if(output[i] != null) {
				count = Math.max(count, output[i].length);
				if(count != output[i].length)
					throw new RuntimeException("Uneven channel count");
			}
		}

		return count;
	}
}