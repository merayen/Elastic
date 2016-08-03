package net.merayen.elastic.client.midi_input;

import net.merayen.elastic.backend.buffer.ObjectCircularBuffer;
import net.merayen.elastic.backend.midi.MidiStatuses;
import net.merayen.elastic.backend.midi.devices.IMIDIReceiver;

/*
 * Receives MIDI and put it into a buffer. Very simple and stupid for now.
 * Sends everything in the buffer whenever requested.
 * TODO Maybe buffer up some seconds
 * TODO Also discard "old" MIDI messages that are like older than 100ms e.g
 */
class MidiReceiver implements IMIDIReceiver {
	public static class MidiPacket {
		short[] midi;
		long time;
	}

	private final ObjectCircularBuffer<MidiPacket> buffer = new ObjectCircularBuffer<>(128);

	@Override
	public void onReceive(short[] midi, long timeStamp) {
		short status = midi[0];

		if(status == MidiStatuses.KEY_DOWN && midi[2] > 0) {
			System.out.printf("Key down: %d, %d\n", midi[1], midi[2]);
		} else if(status == MidiStatuses.KEY_UP || (status == MidiStatuses.KEY_DOWN && midi[2] == 0)) {
			midi[0] = MidiStatuses.KEY_UP; // Translate to KEY_UP if we were given a KEY_DOWN with zero velocity
			System.out.printf("Key up  : %d, %d\n", midi[1], midi[2]);
		} else if(status == MidiStatuses.PITCH_CHANGE) {
			System.out.printf("Pitch change: %d, %d\n", midi[1], midi[2]);
		} else if(status == MidiStatuses.MOD_CHANGE) {
			System.out.printf("Mod change: %d, %d\n", midi[1], midi[2]);
		}

		MidiPacket mp = new MidiPacket();
		mp.midi = midi;
		mp.time = timeStamp;
		buffer.write(mp);
	}

	/*
	 * TODO Skip MIDI packets that are too old (over a certain threshold)
	 */
	public MidiPacket pop() {
		return buffer.read();
	}
}
