package net.merayen.elastic.backend.architectures.local.nodes.output_1;

import net.merayen.elastic.backend.architectures.local.GroupLNode;
import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeOutputData;
import net.merayen.elastic.backend.nodes.BaseNodeProperties;
import net.merayen.elastic.system.intercom.InputFrameData;

import java.util.ArrayList;
import java.util.List;

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
	protected void onProcess(InputFrameData data) {}

	@Override
	protected void onParameter(BaseNodeProperties instance) {}

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
		float[][] channels = new float[channel_count][buffer_size]; // Creating new outgoing buffers to separate the processor and external receivers of this data

		float[] amplitude = new float[channel_count];

		for(int i = 0; i < channel_count; i++)
			offset[i] /= (1 + (buffer_size / (float)sample_rate));

		for(int voice_no = 0; voice_no < output.length; voice_no++) {
			if(output[voice_no] != null) {
				for(int channel_no = 0; channel_no < channel_count; channel_no++) {

					float[] in = output[voice_no][channel_no];
					float[] out = channels[channel_no];
					float voice_amplitude = 0;

					for(int i = 0; i < buffer_size; i++)
						out[i] += in[i];

					// Measure max amplitude
					for(float v : output[voice_no][channel_no]) {
						if(Math.abs(voice_amplitude) < v)
							voice_amplitude = Math.abs(v);

						offset[channel_no] += v;
					}

					amplitude[channel_no] += voice_amplitude;
				}
			}
		}

		GroupLNode parent = (GroupLNode)getParent();
		outgoing = new Output1NodeOutputData(
				getID(),
				channels,
				amplitude,
				offset.clone(),
				parent.getSampleRate(),
				parent.getDepth(),
				parent.getBufferSize());
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