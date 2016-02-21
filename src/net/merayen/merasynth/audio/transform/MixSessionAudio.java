package net.merayen.merasynth.audio.transform;

import java.util.List;

import net.merayen.merasynth.net.util.flow.PortBuffer;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;

/*
 * Mixes multiple sessions into one.
 * TODO mix down to something better than mono!
 */
public class MixSessionAudio {
	/**
	 * Mixes multiple PortBuffers from different node processors.
	 * Mixes as much as possible as long as there is enough data available.
	 * Consumes from the PortBuffers, so their states will be changed.
	 * TODO maybe send in an argument of the channel map we want to mix down to.
	 * TODO Maybe implement events that gets fired when non-AudioResponse-packets are received, like control packets, if those are going to exist
	 */
	public static AudioResponse mix(List<PortBuffer> buffers) {
		int to_send = Integer.MAX_VALUE; // Samples that we can send

		if(buffers.size() == 0)
			return null;

		for(PortBuffer acb : buffers) // Figure out how many samples we are able to mix
			to_send = Math.min(to_send, acb.available());

		if(to_send > 0) {
			PortBuffer.Result[] result_packets = new PortBuffer.Result[buffers.size()];

			// Get all the DataPackets in our span
			for(int i = 0; i < buffers.size(); i++) {
				result_packets[i] = buffers.get(i).get(to_send);
				buffers.get(i).forward(to_send);
			}

			// We mix down to mono for now
			return mixPackets(result_packets, to_send);
		}

		return null;
	}

	private static AudioResponse mixPackets(PortBuffer.Result[] result_packets, int sample_count) { // TODO input channel map
		float output[] = new float[sample_count]; // TODO use channel map, now it is just mono
		int output_offset = 0;

		//List<Integer> next_swap = new ArrayList<>(); // Datapackets that needs to get swapped when 
		int samples_to_process = 0; // Count of samples that we can process until we need to fetch new DataPackets

		AudioResponse[] packets = new AudioResponse[result_packets.length]; // Temporary array to store all input packets for each session/voice
		int[] packets_i = new int[result_packets.length];
		int[] packets_end = new int[result_packets.length];

		int loop_protection = 1000;
		while(output_offset < sample_count && --loop_protection > 0) { // Hmm, if one of the sessions have tight loops, we might fire the loop protection?

			// See if we need to fetch new DataPackets and figure out how many samples it is possible to produce this round
			samples_to_process = Integer.MAX_VALUE;
			for(int i = 0; i < result_packets.length; i++) {
				if(packets_end[i] - packets_i[i] == 0) { // Need new DataPacket
					packets[i] = (AudioResponse)result_packets[i].pop();
					if(packets[i] == null)
						throw new RuntimeException("Should not happen");

					packets_i[i] = result_packets[i].getStart();
					packets_end[i] = result_packets[i].getEnd();
				} else if(packets_end[i] - packets_i[i] < 0) {
					throw new RuntimeException("Should not happen");
				}

				samples_to_process = Math.min(samples_to_process, packets_end[i] - packets_i[i]);
			}

			if(samples_to_process == 0)
				throw new RuntimeException("Should not happen"); // Maybe there has been a zero-length packet, should we allow that?

			// Mix it together. Only mono for now
			for(int i = 0; i < packets.length; i++) {
				AudioResponse r = packets[i];
				for(int j = 0; j < samples_to_process; j++)
					output[output_offset + j] += r.samples[packets_i[i]++];
			}

			output_offset += samples_to_process;
		}
		if(loop_protection == 0)
			throw new RuntimeException("Loop protection");

		if(output_offset != sample_count)
			throw new RuntimeException("Should not happen");

		AudioResponse ar = new AudioResponse();
		ar.channels = new short[]{0}; // Mono for now
		ar.sample_count = sample_count;
		ar.samples = output;

		return ar;
	}
}
