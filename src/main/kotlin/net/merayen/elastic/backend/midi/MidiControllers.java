package net.merayen.elastic.backend.midi;

/**
 * Second byte in the MIDI packet.
 * Control Change Messages (Data Bytes)
 */
public class MidiControllers {
	public static final short DATA_ENTRY_MSB = 6;
	public static final short VOLUME = 7;
	public static final short DATA_ENTRY_LSB = 38;
	public static final short SUSTAIN = 64;
	public static final short RPN_LSB = 100;
	public static final short RPN_MSB = 101;
}
