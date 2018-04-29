package net.merayen.elastic.backend.architectures.local.nodes.midi_spread_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.backend.midi.MidiMessagesCreator;
import net.merayen.elastic.backend.midi.MidiState;
import net.merayen.elastic.util.Postmaster.Message;
import org.jetbrains.annotations.NotNull;

public class LProcessor extends LocalProcessor {
	private MidiInlet input;
	private MidiOutlet output;

	volatile float basePitch;
	volatile private float currentPitch;
	short[] tangent_down;
	private float midiPitch = 0;

	private boolean inited = false;

	private boolean midiHandled;

	private MidiOutlet.MidiFrame midiFrame; // Current one. To be used in the midiState handler

	private MidiState midiState = new MidiState() {
		@Override
		protected void onKeyDown(short tangent, float velocity) {
			output.putMidi(midiFrame.framePosition, getCurrentMidiPacket());
			//output.putMidi(midiFrame.framePosition, MidiMessagesCreator.changePitch(currentPitch));
			tangent_down = getCurrentMidiPacket();
			((LNode)getLocalNode()).updateVoices();
			midiHandled = true;
		}

		@Override
		protected void onPitchChange(float semitones) {
			midiPitch = semitones;
			midiHandled = true;
		}

		@Override
		protected void onPitchBendSensitivityChange(float semitones) {
			output.putMidi(midiFrame.framePosition, MidiMessagesCreator.changePitchBendRange(semitones * 2));
			midiHandled = true;
		}

		@Override
		protected void onMidi(@NotNull short[] midiPacket) {
			if(!midiHandled) // Forward all midi packets we have not handled
				output.putMidi(midiFrame.framePosition, midiPacket);
		}
	};

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

		// We send initial midi to set up
		if(!inited) {
			sendInitialMidi();
			inited = true;
		}

		while((midiFrame = input.getNextMidiFrame(stop)) != null) {
			for(short[] midiPacket : midiFrame) {
				midiHandled = false;
				midiState.handle(midiPacket);
			}
		}

		float newPitch = (basePitch + midiPitch) / (midiState.getBendRange() * 2);
		if(currentPitch != newPitch) { // Pitch offset has been updated. We need to send a new basePitch now.
			currentPitch = newPitch;
			output.putMidi(stop - 1, MidiMessagesCreator.changePitch(newPitch));
		}

		output.written += avail;
		output.push();
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}

	private void sendInitialMidi() {
		// Make the basePitch bend range bigger to
		output.putMidi(0, MidiMessagesCreator.changePitchBendRange(4));
	}
}