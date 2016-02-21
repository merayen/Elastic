package net.merayen.merasynth.net.util.flow;

import java.util.ArrayDeque;
import java.util.Arrays;

import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;

/**
 * Buffer packets and allows for reading into packets by chunks.
 * Is separate for every processor's ports.
 *
 * TODO Implement calculation of DataRequest and actual requested sample count.
 * This is to solve if multiple nodes are connected that request e.g 128 samples each, which
 * ends up being a 256 samples request, which is wrong.
 */
public class PortBuffer implements Iterable<DataPacket> {
	public static class Result {
		public final DataPacket[] datapacket;
		private int position = -1;
		private int first_packet_offset_start; // Offset start into the first DataPacket
		private int last_packet_offset_end; // Offset end into the last DataPacket

		Result(DataPacket[] a, int b, int c) {
			datapacket = a;
			first_packet_offset_start = b;
			last_packet_offset_end = c;
		}

		/**
		 * Retrieves next DataPacket. Returns null if nothing is left
		 */
		public DataPacket pop() {
			if(position == datapacket.length - 1)
				return null; // Nothing left

			return datapacket[++position];
		}

		public int getStart() {
			if(position == -1)
				throw new RuntimeException("Programming error. Call getNext() to get the offset into the datapacket");

			if(position == 0)
				return first_packet_offset_start;

			return 0;
		}

		public int getEnd() {
			if(position == -1)
				throw new RuntimeException("Programming error. Call pop() to get the offset into the datapacket");

			if(position == datapacket.length - 1) // Last packet, we might not send everything of it out
				return last_packet_offset_end;

			return datapacket[position].sample_count;
		}

		public boolean isEmpty() {
			return position == datapacket.length - 1;
		}
	}

	private final ArrayDeque<DataPacket> buffer = new ArrayDeque<DataPacket>();
	private int sample_offset; // Sample offset inside first datapacket
	private int available;

	public void add(DataPacket dp) {
		buffer.add(dp);
		available += dp.sample_count;
	}

	/**
	 * Retrieves all packets upto sample_count, without changing anything.
	 * Call forward(...) to forward the buffer and discard read packets.
	 */
	public Result get(int sample_count) {
		if(sample_count > available)
			throw new RuntimeException("Programming error. Can not consume more samples than there is available");

		int end_offset = sample_offset + sample_count;

		// Calculate number of packets to emit
		int packets_to_emit = 0;
		int packet_samples = 0; // Total samples that are sent out (including outside the offsets)
		for(DataPacket dp : buffer) {
			packet_samples += dp.sample_count;
			packets_to_emit++;

			if(packet_samples > end_offset)
				break;
		}

		DataPacket[] packets = new DataPacket[packets_to_emit];
		int i = 0;
		for(DataPacket dp : buffer) {
			packets[i++] = dp;

			if(i == packets_to_emit)
				break;
		}

		if(packets.length > 0) 
			return new Result(packets, sample_offset, packets[packets.length - 1].sample_count - (packet_samples - (sample_offset + sample_count)));
		else
			return new Result(packets, 0, 0);
	}

	public java.util.Iterator<DataPacket> iterator() {
		return buffer.iterator();
	}

	public DataPacket getLast() {
		return buffer.getLast();
	}

	/**
	 * Forward the buffer, deleting any DataPackets if necessary
	 */
	public void forward(int sample_count) {
		if(sample_count > available)
			throw new RuntimeException("Programming error. Can not consume more samples than there is available");

		int end_offset = sample_offset + sample_count;

		int samples_to_remove = 0;
		int packets_to_remove = 0;
		int pos = 0;
		for(DataPacket dp : buffer) {
			pos += dp.sample_count;

			if(pos > end_offset)
				break;

			samples_to_remove += dp.sample_count; 
			packets_to_remove++;
		}

		//samples_to_remove -= sample_offset;

		// Remove from queue
		for(int i = 0; i < packets_to_remove; i++)
			buffer.remove();

		available -= sample_count;
		sample_offset = (end_offset - samples_to_remove);
	}

	public void clear() {
		buffer.clear();
		available = 0;
		sample_offset = 0;
	}

	public Result consume() {
		return null;//consume(available);
	}

	public int available() {
		return available;
	}

	public boolean isEmpty() {
		return buffer.isEmpty();
	}

	private static void nope() {
		throw new RuntimeException("Test failed");
	}

	public static void test() {
		test_simple();
		test_typical_usage();
	}

	public static void test_simple() {
		PortBuffer p  = new PortBuffer();

		DataPacket[] dp = new DataPacket[10];

		dp[0] = new AudioResponse();
		dp[0].sample_count = 10;
		p.add(dp[0]);

		dp[1] = new AudioResponse();
		dp[1].sample_count = 3;
		p.add(dp[1]);

		dp[2] = new AudioResponse();
		dp[2].sample_count = 0;
		p.add(dp[2]);

		dp[3] = new AudioResponse();
		dp[3].sample_count = 0;
		p.add(dp[3]);

		dp[4] = new AudioResponse();
		dp[4].sample_count = 5;
		p.add(dp[4]);

		Result r = p.get(7);

		if(r.pop() != dp[0])
			nope();

		if(r.getStart() != 0)
			nope();

		if(r.getEnd() != 7)
			nope();

		p.forward(7);

		if(p.available() != 18 - 7)
			nope();

		r = p.get(6);

		if(r.pop() != dp[0])
			nope();

		if(r.getStart() != 7)
			nope();

		if(r.getEnd() != 10)
			nope();

		if(r.datapacket[1] != dp[1])
			nope();

		if(r.pop() != dp[1])
			nope();

		if(r.getStart() != 0)
			nope();

		if(r.getEnd() != 3)
			nope();

		if(r.pop() != dp[2])
			nope();

		if(r.getStart() != 0)
			nope();

		if(r.getEnd() != 0)
			nope();

		if(r.pop() != dp[3])
			nope();

		if(r.getStart() != 0)
			nope();

		if(r.getEnd() != 0)
			nope();

		if(r.pop() != dp[4])
			nope();

		if(r.getStart() != 0)
			nope();

		if(r.getEnd() != 0)
			nope();

		p.forward(6);

		if(p.available() != 18 - 7 - 6)
			nope();

		r = p.get(5);

		if(r.pop() != dp[4])
			nope();

		if(r.getStart() != 0)
			nope();

		if(r.getEnd() != 5)
			nope();

		p.forward(5);

		if(p.available() != 0)
			nope();
	}

	/**
	 * Typical usage test.
	 * Also serves as an example on how to use the PortBuffer effectively.
	 */
	public static void test_typical_usage() {
		PortBuffer freq_port  = new PortBuffer();
		PortBuffer amp_port = new PortBuffer();

		AudioResponse dp = new AudioResponse();
		dp.sample_count = 11;
		dp.samples = new float[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		freq_port.add(dp);

		dp = new AudioResponse();
		dp.sample_count = 7;
		dp.samples = new float[]{1000, 1100, 1200, 1300, 1400, 1500, 1600};
		amp_port.add(dp);

		dp = new AudioResponse();
		dp.sample_count = 3;
		dp.samples = new float[]{1700, 1800, 1900};
		amp_port.add(dp);

		// Calculate how much is available on the input ports. We must process all that is available (due to loops, like feedback)
		int available = Math.min(freq_port.available(), amp_port.available());
		float[] output = new float[available];

		Result freq_result = freq_port.get(available);
		Result amp_result = amp_port.get(available);

		int output_i = 0;

		DataPacket freq_packet = null;
		DataPacket amp_packet = null;
		float[] freq_samples = null;
		float[] amp_samples = null;
		int freq_i = 0;
		int amp_i = 0;
		int freq_end = 0;
		int amp_end = 0;
		int loop_protection = 1000;
		while(--loop_protection > 0) { // Loops until all packets are processed, or the endless-loop protection kicks in  

			if(freq_packet == null || freq_i == freq_end) {
				freq_packet = freq_result.pop();
				if(freq_packet == null)
					break;

				if(freq_packet instanceof AudioResponse) {
					
					freq_i = freq_result.getStart();
					freq_end = freq_result.getEnd();
					freq_samples = ((AudioResponse)freq_packet).samples;
				} else {
					// Handle control packet here, if applicable

					// Gets next packet
					freq_packet = null;
					continue;
				}
			}

			if(amp_packet == null || amp_i == amp_end) {
				amp_packet = amp_result.pop();
				if(amp_packet == null)
					break;

				if(amp_packet instanceof AudioResponse) {
					amp_i = amp_result.getStart();
					amp_end = amp_result.getEnd();
					amp_samples = ((AudioResponse)amp_packet).samples;
				} else {
					// Handle control packet here, if applicable

					// Gets next packet
					amp_packet = null;
					continue;
				}
			}

			while(freq_i < freq_end && amp_i < amp_end) // Processing inner loop. Leaves loop when one of the DataPackets are read until the end
				output[output_i++] = freq_samples[freq_i++] + amp_samples[amp_i++];
		}

		if(loop_protection == 0)
			throw new RuntimeException("Loop protection");

		// Forward the ports
		freq_port.forward(available);
		amp_port.forward(available);

		System.out.println(Arrays.toString(output));

		for(int i = 0; i < available; i++)
			if(output[i] != 1000f + i + i*100)
				System.out.println("Nope at " + i + " with " + output[i]);
	}
}
