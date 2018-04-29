package net.merayen.elastic.backend.midi;

public class MidiMessagesCreator {
	public static short[][] changePitchBendRange(float semitones) {
		return new short[][]{
				{MidiStatuses.MOD_CHANGE, MidiControllers.RPN_LSB, 0},
				{MidiStatuses.MOD_CHANGE, MidiControllers.RPN_MSB, 0},
				{MidiStatuses.MOD_CHANGE, MidiControllers.DATA_ENTRY_MSB, (short)semitones},
				{MidiStatuses.MOD_CHANGE, MidiControllers.DATA_ENTRY_LSB, 0} // Not supporting cents. FIXME
		};
	}

	public static short[] changePitch(float value) {
		value = Math.min(1, Math.max(-1, value));
		if(value < 0)
			return new short[]{MidiStatuses.PITCH_CHANGE, 0, (short)(127 + value * 63)};
		else
			return new short[]{MidiStatuses.PITCH_CHANGE, 0, (short)(value * 63)};
	}
}
