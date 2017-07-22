package net.merayen.elastic.backend.midi;

public class MidiStatuses {
	// First byte
	public static final short KEY_DOWN = 144;
	public static final short KEY_UP = 128;
	public static final short PITCH_CHANGE = 224;
	public static final short MOD_CHANGE = 176;

	// Second byte
	public static final short SUSTAIN = 64;
}
