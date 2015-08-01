package net.merayen.merasynth.client.midi_input;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public class MidiReceiver implements Receiver {

	@Override
	public void send(MidiMessage message, long timeStamp) {
		//System.out.printf("MIDI message: %s [%d]\n", message, message.getStatus());

		int status = message.getStatus();
		byte[] mess = message.getMessage();
		
		if(status == 144) {
			// Keypress down
			int key_no = mess[1];
			int key_velocity = mess[2];
			System.out.printf("Key down: %d, %d\n", mess[1], mess[2]);
		} else if(status == 128) {
			// Keypress up
			int key_no = mess[1];
			System.out.printf("Key up  : %d, %d\n", mess[1], mess[2]);
		} else if(status == 224) {
			// Pitch change
			int value = mess[2]; // Not sure about mess[1]...
			System.out.printf("Pitch change: %d, %d\n", mess[1], mess[2]);
		} else if(status == 176) {
			// Mod change
			int value = mess[2]; // Not sure about mess[1]...
			System.out.printf("Mod change: %d, %d\n", mess[1], mess[2]);
		}
	}

	@Override
	public void close() {}

}
