package net.merayen.elastic.backend.architectures.local.nodes.adsr_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.backend.midi.MidiControllers;
import net.merayen.elastic.backend.midi.MidiState;
import net.merayen.elastic.backend.midi.MidiStatuses;

import java.util.ArrayList;
import java.util.List;

public class LProcessor extends LocalProcessor {
	private MidiOutlet output;
	private MidiInlet input;
	private LNode lnode;

	private short[] current_tangent_down;
	private short[] current_tangent_up;

	private boolean handledMidiPacket;

	private MidiOutlet.MidiFrame midiFrame;

	private MidiState midiState = new MidiState() {
		@Override
		protected void onKeyDown(short tangent, float velocity) {
			current_tangent_up = null;
			current_tangent_down = getCurrentMidiPacket();
			output.putMidi(midiFrame.framePosition, getCurrentMidiPacket());
			output.putMidi(midiFrame.framePosition, new short[]{MidiStatuses.MOD_CHANGE, MidiControllers.CHANNEL_VOLUME_MSB, 0}); // Only if attack is more than 0?
			getADSR().push(position + midiFrame.framePosition, 1);
			keys_down.add(getCurrentMidiPacket()[1]);
			handledMidiPacket = true;
		}

		@Override
		protected void onKeyUp(short tangent) {
			keys_down.removeIf(aShort -> aShort == getCurrentMidiPacket()[1]);

			if (keys_down.isEmpty() && current_tangent_up == null) {
				current_tangent_up = getCurrentMidiPacket();
				getADSR().push(position + midiFrame.framePosition, -1);
			}

			handledMidiPacket = true;
		}

		@Override
		protected void onVolumeChange(float volume) {
			input_volume = (short) Math.min(127, Math.max(0, getCurrentMidiPacket()[2]));
			handledMidiPacket = true;
		}

		@Override
		protected void onMidi(short[] midiPacket) {
			if(!handledMidiPacket)
				output.putMidi(midiFrame.framePosition, midiPacket);
		}
	};

	private ADSR adsr;
	private List<Short> keys_down = new ArrayList<>();

	private short input_volume = 127;

	private long position;

	@Override
	protected void onInit() {
		output = (MidiOutlet)getOutlet("output");
		input = (getInlet("input") instanceof MidiInlet) ? (MidiInlet)getInlet("input") : null;

		lnode = (LNode)getLocalNode();

		position = -buffer_size;
	}

	@Override
	protected void onPrepare() {
		position += buffer_size;
	}

	@Override
	protected void onProcess() {
		if(input != null && output != null) {
			while((midiFrame = input.getNextMidiFrame()) != null) {

				// Reading and handle/forward incoming MIDI data
				for (short[] midiPacket : midiFrame) {
					handledMidiPacket = false;
					midiState.handle(midiPacket, null);
				}
			}

			for(ADSR.Entry entry : getADSR().process(buffer_size))
				output.putMidi((int) (entry.position - position), new short[]{MidiStatuses.MOD_CHANGE, MidiControllers.CHANNEL_VOLUME_MSB, (short) (entry.state * input_volume)}); // FIXME set the correct position

			if(current_tangent_up != null && getADSR().isNeutral()) {
				output.putMidi(buffer_size - 1, new short[]{MidiStatuses.KEY_UP, current_tangent_up[1], 0});
				current_tangent_up = null;
			}

			output.push();

		} else if(output != null) {
			output.push();
		}
	}

	@Override
	protected void onDestroy() {}

	private ADSR getADSR() {
		if(adsr == null)
			adsr = new ADSR(128, (int) (lnode.attack * sample_rate), (int) (lnode.decay * sample_rate), lnode.sustain, (int) (lnode.release * sample_rate));

		return adsr;
	}
}
