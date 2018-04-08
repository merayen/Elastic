package net.merayen.elastic.backend.architectures.local.nodes.midi_spread_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.backend.midi.MidiStatuses;
import net.merayen.elastic.util.Postmaster.Message;

public class LProcessor extends LocalProcessor {
	private MidiInlet input;
	private MidiOutlet output;

	volatile float pitch;
	volatile private float current_pitch;
	short[] tangent_down;
	private short midi_pitch = 63;

	@Override
	protected void onInit() {
		input = (getInlet("input") instanceof MidiInlet) ? (MidiInlet)getInlet("input") : null;
		output = (getOutlet("output") instanceof MidiOutlet) ? (MidiOutlet)getOutlet("output") : null;
	}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onProcess() {
		if(input != null) {
			int avail = input.available();
			if(output != null && avail > 0) {
				processMidi(avail);
				output.written += input.read;
				output.push();
			}

			input.read += avail;
		}
	}

	private void processMidi(int avail) {
		int start = input.read;
		int stop = start + avail;

		MidiOutlet.MidiFrame midiFrame;
		while((midiFrame = input.getNextMidiFrame(stop)) != null) {
			for(short[] midi_packet : midiFrame) {
				if((midi_packet[0] & 0b11110000) == MidiStatuses.KEY_DOWN) {
					output.putMidi(midiFrame.framePosition, midi_packet);
					output.putMidi(midiFrame.framePosition, new short[]{MidiStatuses.PITCH_CHANGE, 0, (short)(current_pitch * 32 + midi_pitch)});
					tangent_down = midi_packet;
					((LNode)getLocalNode()).updateVoices(); // Re-assign pitches on all
				} else if((midi_packet[0] & 0b11110000) == MidiStatuses.PITCH_CHANGE) {
					midi_pitch = midi_packet[2];
					output.putMidi(midiFrame.framePosition, new short[]{MidiStatuses.PITCH_CHANGE, 0, (short)(current_pitch * 32 + midi_pitch)});
				} else {
					output.putMidi(midiFrame.framePosition, midi_packet);
				}
			}
		}

		if(current_pitch != pitch) { // Pitch offset has been updated. We need to send a new pitch now.
			current_pitch = pitch;
			output.putMidi(stop - 1, new short[]{MidiStatuses.PITCH_CHANGE, 0, (short)(current_pitch * 32 + midi_pitch)});
		}

		output.written += avail;
		output.push();
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}
}