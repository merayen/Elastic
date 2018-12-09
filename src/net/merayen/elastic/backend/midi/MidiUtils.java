package net.merayen.elastic.backend.midi;

public class MidiUtils {
	private MidiUtils() {}

	public static float midiPitchToFloat(short[] midi) { // TODO care about LSB
		return (midi[2] < 64 ? midi[2] : (midi[2] - 127)) / 32f;
	}
}
