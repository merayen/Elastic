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
	private long attack_start = Long.MAX_VALUE;
	private long decay_start = Long.MAX_VALUE;
	private long decay_stop = Long.MAX_VALUE;
	private long release_start = Long.MAX_VALUE;
	private long release_stop = Long.MAX_VALUE;
	private short release_start_volume;
	private long sample_ticks;
	private List<Short> keys_down = new ArrayList<>();

	private short input_volume = 127;
	private short current_volume;

	@Override
	protected void onInit() {
		output = (MidiOutlet)getOutlet("output");
		input = (getInlet("input") instanceof MidiInlet) ? (MidiInlet)getInlet("input") : null;

		lnode = (LNode)getLocalNode();
	}

	@Override
	protected void onPrepare() {
		if(output != null)
			for(int i = 0; i < buffer_size; i++)
				output.midi[i] = null;
	}

	@Override
	protected void onProcess() {
		if(input != null && output != null) {
			float sustain = lnode.sustain;
			int stop = input.outlet.written;
			final List<short[]> outgoing = new ArrayList<>();
			for(int i = input.read; i < stop; i++) {
				outgoing.clear();

				// Reading and handle/forward incoming MIDI data
				if(input.outlet.midi[i] != null) {
					for(short[] midi_packet : input.outlet.midi[i]) {
						if((midi_packet[0] & 0b11110000) == MidiStatuses.KEY_DOWN) {
							attack_start = sample_ticks;
							decay_start = sample_ticks + (long)((double)lnode.attack * sample_rate);
							decay_stop = decay_start + (long)((double)lnode.decay * sample_rate);
							release_start = Long.MAX_VALUE;
							release_stop = Long.MAX_VALUE;
							release_start_volume = 0;
							current_tangent_up = null;
							current_tangent_down = midi_packet;
							outgoing.add(midi_packet);
							outgoing.add(new short[] {MidiStatuses.MOD_CHANGE, MidiControllers.VOLUME, 0}); // Only if attack is more than 0?
							keys_down.add(midi_packet[1]);
						} else if((midi_packet[0] & 0b11110000) == MidiStatuses.KEY_UP) { // Also detect KEY_DOWN with 0 velocity!
							Iterator<Short> iter = keys_down.iterator();
							while(iter.hasNext())
								if(iter.next() == midi_packet[1])
									iter.remove();

							if(keys_down.isEmpty()) {
								release_start = sample_ticks;
								release_stop = sample_ticks + (long) ((double) lnode.release * sample_rate);
								release_start_volume = current_volume;
								current_tangent_up = midi_packet;
							} else {
								outgoing.add(new short[]{MidiStatuses.KEY_UP, midi_packet[1], 0});
							}
						} else if((midi_packet[0] & 0b11110000) == MidiStatuses.MOD_CHANGE && midi_packet[1] == MidiControllers.VOLUME) {
							input_volume = (short)Math.min(127, Math.max(0, midi_packet[2]));
						} else { // Forward everything else
							outgoing.add(midi_packet);
						}
					}
				}

				// Sending of volume information, via MIDI
				float new_volume;
				if(release_stop <= sample_ticks) {
					new_volume = 0; // TODO send KEY_UP once? Really?
					if(current_tangent_up != null) {
						outgoing.add(current_tangent_up); // Forward the tangent up event we got previously sometime.
						current_tangent_up = null;
					}
				} else if(release_start <= sample_ticks) { // TODO move
					new_volume = (release_start_volume / 127f) - (float)Math.pow((sample_ticks - release_start) / (float)(release_stop - release_start), 2) * ((release_start_volume / 127f));
				} else if(decay_stop <= sample_ticks) {
					new_volume = sustain;
				} else if(decay_start <= sample_ticks) {
					new_volume = 1 - ((sample_ticks - decay_start) / (float)(decay_stop - decay_start)) * (1 - sustain);
				} else if(attack_start <= sample_ticks) {
					new_volume = (sample_ticks - attack_start) / (float)(decay_start - attack_start);
				} else {
					new_volume = 0;
				}

				new_volume *= input_volume;

				if((short)new_volume != current_volume) {
					current_volume = (short)new_volume;
					outgoing.add(new short[] {MidiStatuses.MOD_CHANGE, MidiControllers.VOLUME, current_volume});
					//System.out.println("Pappan din: " + new_volume);
				}

				if(!outgoing.isEmpty())
					output.midi[i] = outgoing.toArray(new short[outgoing.size()][]);

				sample_ticks++;
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
}
