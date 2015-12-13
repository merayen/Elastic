package net.merayen.merasynth.netlist.util.flow;

import java.util.ArrayDeque;
import java.util.List;

import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;

public class PortFlow {
	private final ArrayDeque<DataPacket> buffer = new ArrayDeque<DataPacket>();
	private int sample_offset; // Sample offset inside first datapacket
	private int available;

	public void add(DataPacket dp) {
		buffer.add(dp);
		available += dp.sample_count;
	}

	/**
	 * Consumes a single packet. Only part of it if max_sample_count is less than its size+offset.
	 * Caller needs to call this function multiple times with a decreasing max_sample_count until
	 * it is satisfied.
	 * Check with available() before you start to see if buffer contains enough data at all, it is
	 * a programming error to ask for higher counts of samples than it is available.
	 */
	public DataPacket consume(int max_sample_count) {
		if(max_sample_count > available)
			throw new RuntimeException("Programming error. Can not consume more samples than there is available");

		DataPacket next = buffer.peekFirst();
		if(next.sample_count == 0) { // Packet has no length, so no reason to update us
			if(sample_offset != 0)
				throw new RuntimeException("Should not happen");

			return buffer.removeFirst();
		} else {
			if(next.sample_count <= sample_offset)
				throw new RuntimeException("Should not happen");

			// Update our available and offset values
			if(max_sample_count < next.sample_count - sample_offset) { // We end up inside a package, so we have not delivered it "fully"
				available -= max_sample_count;
				sample_offset += max_sample_count;
				return next;
			} else { // Nope, we return the packet "fully"
				available -= next.sample_count - sample_offset;
				sample_offset = 0;
				return buffer.removeFirst();
			}
		}
	}

	public int available() {
		return available;
	}

	public static void test() {
		PortFlow pf = new PortFlow();
		DataPacket[] dp = new DataPacket[4];

		dp[0] = new AudioResponse();
		dp[0].sample_count = 10;
		pf.add(dp[0]);

		dp[1] = new AudioResponse();
		dp[1].sample_count = 5;
		pf.add(dp[1]);

		dp[2] = new AudioResponse();
		dp[2].sample_count = 2;
		pf.add(dp[2]);

		if(pf.available() != 17)
			throw new RuntimeException("Wrong available()");

		if(pf.consume(7) != dp[0])
			throw new RuntimeException("Nope 0");

		if(pf.available() != 17 - 7)
			throw new RuntimeException("Wrong available()");

		if(pf.consume(2) != dp[0])
			throw new RuntimeException("Nope 1");

		if(pf.consume(2) != dp[0])
			throw new RuntimeException("Nope 2");

		if(pf.consume(pf.available()) != dp[1])
			throw new RuntimeException("Nope 3");

		if(pf.available() != 2)
			throw new RuntimeException("Wrong available()");

		if(pf.consume(2) != dp[2])
			throw new RuntimeException("Nope 3");
	}
}
