package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator_1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.backend.architectures.local.utils.InputSignalParametersProcessor;
import net.merayen.elastic.backend.midi.MidiStatuses;
import net.merayen.elastic.util.Postmaster.Message;

/*
 * Makes beeping sounds.
 * TODO We need to rethink about data streams. We need a handler class that can help Processors to know
 * how much they should produce, like remember the amount requested, as incoming data might be much more than
 * we have requested due to ports being split.
 */
public class LProcessor extends LocalProcessor {
	private enum Mode {
		NOTHING,
		RAW,
		MIDI,
		FREQUENCY,
		MALFUNCTION // E.g, wrong line has been connected. We output only zeroes in this case (and should send a warning)
	}

	private LNode lnode;

	private Mode mode;

	private float[][] input_frequency_buffer;

	List<short[]> keys_down = new ArrayList<>();

	private double pos = 0;

	@Override
	protected void onInit() {
		lnode = (LNode)getLocalNode();

		Inlet frequency = getInlet("frequency");
		Outlet output = getOutlet("output");

		if(output == null)
			mode = Mode.NOTHING;
		if(frequency == null) {
			mode = Mode.RAW;
		} else if(frequency != null) {
			if(frequency instanceof AudioInlet) {
				mode = Mode.FREQUENCY;
			} else if(frequency instanceof MidiInlet) {
				mode = Mode.MIDI;
			} else {
				mode = Mode.MALFUNCTION; // We don't understand the input on the frequency-port
			}
		}
	}

	@Override
	public void onProcess() {
		if(getOutlet("output") == null)
			return;

		if(mode == Mode.RAW)
			generateRaw();
		else if(mode == Mode.FREQUENCY)
			generateWithFrequency();
		else if(mode == Mode.MIDI)
			generateWithMidi();
		else
			throw new RuntimeException("Not implemented");
	}

	private void generateRaw() {
		AudioOutlet outlet = (AudioOutlet)getOutlet("output");

		outlet.setChannelCount(1);

		double step = (lnode.frequency * Math.PI * 2) / (double)sample_rate;
		//float avg = 0;
		for(int i = outlet.written; i < outlet.buffer_size; i++) {
			outlet.audio[0][i] = lnode.curve_wave[(int)((pos / (Math.PI * 2) * lnode.curve_wave.length) % lnode.curve_wave.length)] - 0.5f;
			pos += step;
			//avg += outlet.audio[0][i];
		}
		//System.out.println("Hopp " + avg / (float)outlet.buffer_size);

		outlet.written = outlet.buffer_size;
		outlet.push();
	}

	private void generateWithFrequency() {
		AudioOutlet outlet = (AudioOutlet)getOutlet("output");
		AudioInlet frequency = (AudioInlet)getInlet("frequency");

		outlet.setChannelCount(1);

		int available = available();

		if(available == 0)
			return;

		// Transform input according to the UI's InputSignalProcessor() parameters
		if(input_frequency_buffer == null)
			input_frequency_buffer = new float[1][buffer_size];

		InputSignalParametersProcessor.process(lnode, "frequency", new float[][]{frequency.outlet.audio[0]}, input_frequency_buffer, outlet.written, available);

		int i;
		for(i = outlet.written; i < outlet.written + available; i++) {
			outlet.audio[0][i] = lnode.curve_wave[Math.floorMod((int)(pos / (Math.PI * 2 * sample_rate) * lnode.curve_wave.length), lnode.curve_wave.length)];
			pos += input_frequency_buffer[0][i];
		}

		outlet.written = i;
		frequency.read = i;
		outlet.push();
	}

	private void generateWithMidi() {
		AudioOutlet outlet = (AudioOutlet)getOutlet("output");
		MidiInlet inlet = (MidiInlet)getInlet("frequency");
		int available = inlet.available();

		if(available == 0)
			return;

		updateKeysDown();

		outlet.setChannelCount(1);

		if(!keys_down.isEmpty()) {
			short[] active_key = keys_down.get(keys_down.size() - 1);
			double freq = midiNoteToFreq(active_key[1]) / sample_rate; // TODO take care of pitch wheel

			for(int i = outlet.written; i < outlet.written + available; i++) {
				outlet.audio[0][i] = lnode.curve_wave[Math.floorMod((int)(pos * lnode.curve_wave.length), lnode.curve_wave.length)];
				pos += freq;
			}
		} else { // No key down? Silence!
			pos = 0;
			for(int ch = 0; ch < outlet.audio.length; ch++)
				for(int i = 0; i < buffer_size; i++)
					outlet.audio[ch][i] = 0;
		}

		outlet.written += available;
		outlet.push();
	}

	private void updateKeysDown() {
		MidiInlet inlet = (MidiInlet)getInlet("frequency");
		if(inlet.available() > 0) {
			short[][][] midi = ((MidiOutlet)inlet.outlet).midi;

			for(short[][] sample : midi) {
				if(sample != null) {
					for(short[] midi_packet : sample) {
						if((midi_packet[0] & MidiStatuses.KEY_DOWN) == MidiStatuses.KEY_DOWN) {
							keys_down.add(midi_packet);
						} else if((midi_packet[0] & MidiStatuses.KEY_UP) == MidiStatuses.KEY_UP) { // Also detect KEY_DOWN with 0 velocity!
							Iterator<short[]> iter = keys_down.iterator();
							while(iter.hasNext()) {
								short[] m = iter.next();
								if(m[1] == midi_packet[1])
									iter.remove();
							}
						}
					}
				}
			}

			inlet.read += inlet.available();
		}
	}

	private double midiNoteToFreq(short n) {
		return 440 * Math.pow(2, (n - 69) / 12.0f);
	}

	@Override
	protected void onPrepare() {}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}
}