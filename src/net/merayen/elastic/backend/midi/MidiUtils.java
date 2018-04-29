package net.merayen.elastic.backend.midi;

import org.jetbrains.annotations.NotNull;

public class MidiUtils {
	private MidiUtils() {}

	public static float midiPitchToFloat(@NotNull short[] midi) { // TODO care about LSB
		return (midi[2] < 64 ? midi[2] : (midi[2] - 127)) / 32f;
	}
}
