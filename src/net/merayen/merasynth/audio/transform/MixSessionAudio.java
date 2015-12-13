package net.merayen.merasynth.audio.transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.merayen.merasynth.buffer.AudioCircularBuffer;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;

/*
 * Mixes multiple sessions into one.
 * TODO mix down to something better than mono!
 */
public class MixSessionAudio {
	public AudioResponse mix(List<AudioCircularBuffer> buffers) {
		AudioResponse ar = new AudioResponse();

		int to_send = Integer.MAX_VALUE;
		List<Short> channels = new ArrayList<>();

		if(buffers.size() == 0)
			return null;

		for(AudioCircularBuffer acb : buffers) {
			int available = acb.available();

			if(available == 0)
				return null; // Not received from all processors/sessions yet. We wait

			to_send = Math.min(to_send, available);
			channels.addAll(acb.getChannels());
		}

		channels = new ArrayList<Short>(new HashSet<Short>(channels)); // Remove duplicate channels

		float[] samples = new float[to_send];
		float[] channel_samples = new float[to_send];

		for(AudioCircularBuffer acb : buffers) {
			acb.engage();
			for(Short channel : acb.getChannels()) // Read audio from channel
				acb.read(channel, channel_samples);

			for(int i = 0; i < to_send; i++) // Mix the audio
				samples[i] += channel_samples[i] / channels.size();

			acb.disengage();
		}

		ar.channels = new short[]{0}; // TODO Allow mixing to something else than mono
		ar.sample_count = to_send;
		ar.samples = samples;

		return ar;
	}
}
