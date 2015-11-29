package net.merayen.merasynth.client.mix;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.merayen.merasynth.buffer.AudioCircularBuffer;
import net.merayen.merasynth.netlist.util.flow.AudioFlowHelper;

class Mixer {
	final AudioFlowHelper audio_flow_helper;

	public Mixer(AudioFlowHelper aif) {
		this.audio_flow_helper = aif;
	}

	public void mix(float fac_value) {
		int to_send = Math.min(audio_flow_helper.available("input_a"), audio_flow_helper.available("input_b"));
		if(to_send == 0)
			return;

		AudioCircularBuffer aif_a = audio_flow_helper.getInputBuffer("input_a");
		AudioCircularBuffer aif_b = audio_flow_helper.getInputBuffer("input_b");
		List<Short> input_a_channels = aif_a.getChannels();
		List<Short> input_b_channels = aif_b.getChannels();

		short[] output_channels = getOutputChannels(input_a_channels, input_b_channels);

		System.out.printf("Channels: %s\n", output_channels);

		final float mix_a_fac = fac_value;
		final float mix_b_fac = 1 - fac_value;
		float[] output = new float[output_channels.length * to_send];

		// Mixing input sources
		float[] input_a_buff = new float[to_send];
		float[] input_b_buff = new float[to_send];
		for(int channel_index = 0; channel_index < output_channels.length; channel_index++) {
			int channel = output_channels[channel_index];
			if(input_a_channels.contains((short)channel) && input_b_channels.contains((short)channel)) {
				aif_a.read(channel, input_a_buff);
				aif_b.read(channel, input_b_buff);

				for(int i = 0; i < to_send; i++) 
					output[to_send * channel_index + i] = input_a_buff[i] * mix_a_fac + input_b_buff[i] * mix_b_fac;

			} else {
				throw new RuntimeException("Not implemented yet to handle uneven channel number in input");
			}
		}

		audio_flow_helper.send("output", output_channels, output);
	}

	short[] getOutputChannels(List<Short> input_a_channels, List<Short> input_b_channels) {
		Set<Short> output_channels = new HashSet<Short>(); // TODO any better Set()?

		// Figure out all channels
		for(short x : input_a_channels)
			output_channels.add(x);

		for(short x : input_b_channels)
			output_channels.add(x);

		short[] result = new short[output_channels.size()];
		int i = 0;
		for(short x : output_channels)
			result[i++] = x;

		return result;
	}
}
