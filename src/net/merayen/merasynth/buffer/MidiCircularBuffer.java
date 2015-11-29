package net.merayen.merasynth.buffer;

import net.merayen.merasynth.netlist.datapacket.MidiResponse;

public class MidiCircularBuffer {
	private final ObjectCircularBuffer<MidiResponse> buffer;

	public class MidiPacket {
		short[] midi;
		long space_before; // How many sample space before this MIDI event
	}

	private long position;

	public MidiCircularBuffer() {
		buffer = new ObjectCircularBuffer<MidiResponse>(512);
	}

	public void write(MidiResponse packet) {
		buffer.write(packet);
	}

	public void read(int sample_count) {
		
	}
}
