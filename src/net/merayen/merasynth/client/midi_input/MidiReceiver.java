package net.merayen.merasynth.client.midi_input;

import java.util.ArrayList;

import net.merayen.merasynth.midi.devices.IMIDIReceiver;
import net.merayen.merasynth.netlist.datapacket.MidiResponse;

/*
 * Recieves MIDI and put it into a buffer. Very simple and stupid for now.
 * Sends everything in the buffer whenever requested.
 * TODO Maybe buffer up some seconds
 * TODO ALso discard "old" MIDI messages that are like older than 100ms e.g
 */
public class MidiReceiver implements IMIDIReceiver {
	private static class MidiPacket {
		short[] midi;
	}

	public ArrayList<MidiPacket> buffer = new ArrayList<MidiPacket>();

	@Override
	public void onReceive(short[] midi, long timeStamp) {
		short status = midi[0];

		if(status == 144) {
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
		}
	}

	/*
	 * Retrieves MIDI data that is waiting in the buffer, if any.
	 */
	public MidiResponse retrieve(int sample_count) {
		MidiResponse mr = new MidiResponse();
		mr.sample_count = sample_count;
		mr.midi = new short[buffer.size()][];
		mr.offset = new int[buffer.size()]; // Zeroes for now, everything at once
		mr.channels = new short[buffer.size()]; // Send everything at channel 0. TODO Intelligently latch and choose channels

		for(int i = 0; i < buffer.size(); i++)
			mr.midi[i] = buffer.get(i).midi;

		buffer.clear();

		return mr;
	}
}
