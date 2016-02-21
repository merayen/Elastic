package net.merayen.merasynth.net.util.flow;

import net.merayen.merasynth.netlist.datapacket.DataPacket;

/**
 * Helps iterating through multiple ports.
 * 
 *  Note: Only allows linear iterating, which means that you can't read PortBuffers unsynchronous.
 */
public class PortBufferIterator {
	public interface IteratorFunc {
		/**
		 * PortBufferIterator calculates how many samples that can be processed by your function before one or more datapackets needs to be fetched.
		 * @param port_packets All the datapackets that is to process in this round
		 * @param packet_offsets Offset into the datapackets
		 * @param offset Offset into processing
		 * @param sample_count Count of samples that can be processed this round
		 */
		public void loop(DataPacket[] port_packets, int[] packet_offsets, int offset, int sample_count);
	}

	public static class Stop extends RuntimeException {};

	// Variables for if forward() is getting called afterwards.
	private int samples_processed;
	private PortBuffer[] buffers;

	public PortBufferIterator(PortBuffer[] buffers, IteratorFunc func) {
		this.buffers = buffers;
		if(buffers.length == 0)
			return;

		int to_send = Integer.MAX_VALUE; // Samples that we can send

		for(PortBuffer acb : buffers) // Figure out how many samples we are able to mix
			to_send = Math.min(to_send, acb.available());

		if(to_send == 0)
			return;

		samples_processed = to_send;

		PortBuffer.Result[] result_packets = new PortBuffer.Result[buffers.length];

		// Get all the DataPackets in our span
		for(int i = 0; i < buffers.length; i++)
			result_packets[i] = buffers[i].get(to_send);

		int output_offset = 0;

		//List<Integer> next_swap = new ArrayList<>(); // Datapackets that needs to get swapped when 
		int samples_to_process = 0; // Count of samples that we can process until we need to fetch new DataPackets

		DataPacket[] packets = new DataPacket[result_packets.length]; // Temporary array to store all active input packets
		int[] packets_i = new int[result_packets.length];
		int[] packets_end = new int[result_packets.length];

		int loop_protection = 1000;
		while(output_offset < to_send && --loop_protection > 0) { // Hmm, if one of the sessions have tight loops, we might fire the loop protection?

			// See if we need to fetch new DataPackets and figure out how many samples it is possible to produce this round
			samples_to_process = Integer.MAX_VALUE;
			for(int i = 0; i < result_packets.length; i++) {
				if(packets_end[i] - packets_i[i] == 0) { // Need new DataPacket
					packets[i] = result_packets[i].pop();
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

			// Iterate once
			try {
				func.loop(packets, packets_i, output_offset, samples_to_process);
			} catch (Stop e) {
				return;
			}

			// Forward the offsets (untested, but I think this is right)
			for(int i = 0; i < packets_i.length; i++)
				packets_i[i] += samples_to_process;

			output_offset += samples_to_process;
		}
		if(loop_protection == 0)
			throw new RuntimeException("Loop protection");

		if(output_offset != to_send)
			throw new RuntimeException("Should not happen");
	}

	/**
	 * Helper function that forwards all the buffers that has been iterated, with the actual processed amount of samples.
	 */
	public void forward() {
		for(PortBuffer pb : buffers)
			pb.forward(samples_processed);

		samples_processed = 0;
	}
}
