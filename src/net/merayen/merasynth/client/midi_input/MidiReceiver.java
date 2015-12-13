package net.merayen.merasynth.client.midi_input;

import net.merayen.merasynth.buffer.ObjectCircularBuffer;
import net.merayen.merasynth.midi.devices.IMIDIReceiver;

/*
 * Recieves MIDI and put it into a buffer. Very simple and stupid for now.
 * Sends everything in the buffer whenever requested.
 * TODO Maybe buffer up some seconds
 * TODO ALso discard "old" MIDI messages that are like older than 100ms e.g
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

		/*if(status == 144) {
			// Keypress down
			int key_no = midi[1];
			int key_velocity = midi[2];
			System.out.printf("Key down: %d, %d\n", midi[1], midi[2]);
		} else if(status == 128) {
			// Keypress up
			int key_no = midi[1];
			System.out.printf("Key up  : %d, %d\n", midi[1], midi[2]);
		} else if(status == 224) {
			// Pitch change
			int value = midi[2]; // Not sure about mess[1]...
			System.out.printf("Pitch change: %d, %d\n", midi[1], midi[2]);
		} else if(status == 176) {
			// Mod change
			int value = midi[2]; // Not sure about mess[1]...
			System.out.printf("Mod change: %d, %d\n", midi[1], midi[2]);
		}*/

		if(midi == null)
			throw new RuntimeException("NBei har du sett");

		MidiPacket mp = new MidiPacket();
		mp.midi = midi;
		mp.time = timeStamp;
		buffer.write(mp);
	}

	/*
	 * TODO Skip midi packets that are too old (over a certain thresahold)
	 */
	public MidiPacket pop() {
		return buffer.read();
	}
}
