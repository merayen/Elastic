package net.merayen.elastic.backend.architectures.local.nodes.adsr_1;

import java.util.*;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.backend.midi.MidiControllers;
import net.merayen.elastic.backend.midi.MidiStatuses;
import net.merayen.elastic.util.Postmaster.Message;

public class LProcessor extends LocalProcessor {
	private MidiOutlet output;
	private MidiInlet input;
	private LNode lnode;

	private short[] current_tangent_down;
	private short[] current_tangent_up;

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
			int stop = input.outlet.written;
			int lastSamplePosition = 0;

			MidiOutlet.MidiFrame midiFrame;
			while((midiFrame = input.getNextMidiFrame(stop)) != null) {
				lastSamplePosition = midiFrame.framePosition;

				// Reading and handle/forward incoming MIDI data
				for (short[] midiPacket : midiFrame) {
					if ((midiPacket[0] & 0b11110000) == MidiStatuses.KEY_DOWN) {
						current_tangent_up = null;
						current_tangent_down = midiPacket;
						output.putMidi(midiFrame.framePosition, midiPacket);
						output.putMidi(midiFrame.framePosition, new short[]{MidiStatuses.MOD_CHANGE, MidiControllers.VOLUME, 0}); // Only if attack is more than 0?
						getADSR().push(position + lastSamplePosition, 1);
						keys_down.add(midiPacket[1]);
					} else if ((midiPacket[0] & 0b11110000) == MidiStatuses.KEY_UP) { // Also detect KEY_DOWN with 0 velocity!
						Iterator<Short> iter = keys_down.iterator();
						while (iter.hasNext())
							if (iter.next() == midiPacket[1])
								iter.remove();

						if (keys_down.isEmpty()) {
							current_tangent_up = midiPacket;
							getADSR().push(position + lastSamplePosition, -1);
						} else {
							output.putMidi(midiFrame.framePosition, new short[]{MidiStatuses.KEY_UP, midiPacket[1], 0});
						}
					} else if ((midiPacket[0] & 0b11110000) == MidiStatuses.MOD_CHANGE && midiPacket[1] == MidiControllers.VOLUME) {
						input_volume = (short) Math.min(127, Math.max(0, midiPacket[2]));
					} else { // Forward everything else
						output.putMidi(midiFrame.framePosition, midiPacket);
					}
				}
			}

			for(ADSR.Entry entry : getADSR().process(input.available())) {
				output.putMidi((int) (entry.position - position), new short[]{MidiStatuses.MOD_CHANGE, MidiControllers.VOLUME, (short) (entry.state * input_volume)}); // FIXME set the correct position
			}

			if(current_tangent_up != null && getADSR().isNeutral()) {
				output.putMidi(buffer_size - 1, new short[]{MidiStatuses.KEY_UP, current_tangent_up[1], 0});
				current_tangent_up = null;
			}

			input.read = stop;
			output.written = stop;
			output.push();

		} else if(input != null) {
			input.read = input.outlet.written;
		} else if(output != null) {
			output.written = buffer_size;
		}
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}

	private ADSR getADSR() {
		if(adsr == null)
			adsr = new ADSR(128, (int) (lnode.attack * sample_rate), (int) (lnode.decay * sample_rate), lnode.sustain, (int) (lnode.release * sample_rate));

		return adsr;
	}
}
