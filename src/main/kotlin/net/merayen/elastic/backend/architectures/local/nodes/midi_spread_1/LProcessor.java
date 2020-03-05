package net.merayen.elastic.backend.architectures.local.nodes.midi_spread_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.backend.midi.MidiMessagesCreator;
import net.merayen.elastic.backend.midi.MidiState;

import java.util.Map;

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
	private Integer midiInputFramePosition;

	private MidiState midiState = new MidiState() {
		@Override
		protected void onKeyDown(short tangent, float velocity) {
			output.addMidi(midiInputFramePosition, getCurrentMidiPacket());
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
			output.addMidi(midiInputFramePosition, MidiMessagesCreator.INSTANCE.changePitchBendRange(semitones * 2));
			midiHandled = true;
		}

		@Override
		protected void onMidi(short[] midiPacket) {
			if(!midiHandled) // Forward all midi packets we have not handled
				output.addMidi(midiInputFramePosition, midiPacket);
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
		if (frameFinished())
			return;

		if(input != null) {
			if(output != null && input.available()) {
				processMidi(buffer_size);
				output.push();
			}
		}
	}

	private void processMidi(int avail) {
		// We send initial midi to set up
		if(!inited) {
			sendInitialMidi();
			inited = true;
		}

		for (Map.Entry<Integer, MidiOutlet.MidiFrame> entry : input.outlet.midi.entrySet()) {
			midiInputFramePosition = entry.getKey();
			for(short[] midiPacket : midiFrame) {
				midiHandled = false;
				midiState.handle(midiPacket);
			}
		}

		float newPitch = (basePitch + midiPitch) / (midiState.getBendRange() * 2);
		if(currentPitch != newPitch) { // Pitch offset has been updated. We need to send a new basePitch now.
			currentPitch = newPitch;
			output.addMidi(buffer_size - 1, MidiMessagesCreator.INSTANCE.changePitch(newPitch));
		}

		output.push();
	}

	@Override
	protected void onDestroy() {}

	private void sendInitialMidi() {
		// Make the basePitch bend range bigger to
		output.addMidi(0, MidiMessagesCreator.INSTANCE.changePitchBendRange(4));
	}
}