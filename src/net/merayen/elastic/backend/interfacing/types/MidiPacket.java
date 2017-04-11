package net.merayen.elastic.backend.interfacing.types;

public class MidiPacket {
	public short[] midi;
	public int sample_offset; // Offset from the buffer start

	public MidiPacket(short[] midi, int sample_offset) {
		this.midi = midi;
		this.sample_offset = sample_offset;
	}
}
