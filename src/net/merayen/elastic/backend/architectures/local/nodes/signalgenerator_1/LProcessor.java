package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator_1;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.laszlosystems.libresample4j.Resampler;
import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioInlet;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiInlet;
import net.merayen.elastic.backend.architectures.local.lets.MidiOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.backend.architectures.local.nodes.poly_1.SessionKeeper;
import net.merayen.elastic.backend.architectures.local.utils.InputSignalParametersProcessor;
import net.merayen.elastic.backend.midi.MidiControllers;
import net.merayen.elastic.backend.midi.MidiStatuses;
import net.merayen.elastic.util.Postmaster.Message;

public class LProcessor extends LocalProcessor implements SessionKeeper {
	private enum Mode {
		NOTHING,
		RAW,
		MIDI,
		FREQUENCY,
		MALFUNCTION // E.g, wrong line has been connected. We output only zeroes in this case (and should send a warning)
	}

	private LNode lnode;
	private Resampling resampling;
	private FloatBuffer samplerIn, samplerOut;

	private Mode mode;

	private float[][] input_frequency_buffer;

	List<short[]> keys_down = new ArrayList<>();

	private boolean sustain;
	private short[] active_tangent;
	private float pitch;
	private float volume = 1;
	private float new_volume = 1;

	private double pos;

	@Override
	protected void onInit() {
		lnode = (LNode)getLocalNode();

		Inlet frequency = getInlet("frequency");
		Outlet output = getOutlet("output");

		if(output != null)
			((AudioOutlet)output).setChannelCount(1);

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
		if(getOutlet("output") == null) {
			Inlet frequency = getInlet("frequency");
			if(frequency != null)
				frequency.read = frequency.outlet.written;
			return;
		}

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

		if(resampling != null && outlet.written == 0)
			resampling.update(lnode.frequency);

		outlet.written = outlet.buffer_size;
		outlet.push();
	}

	private void generateWithFrequency() {
		AudioOutlet outlet = (AudioOutlet) getOutlet("output");
		AudioInlet frequency = (AudioInlet) getInlet("frequency");

		int available = available();

		if (available == 0)
			return;

		// Transform input according to the UI's InputSignalProcessor() parameters
		if (input_frequency_buffer == null)
			input_frequency_buffer = new float[1][buffer_size];

		InputSignalParametersProcessor.process(lnode, "frequency", new float[][]{frequency.outlet.audio[0]}, input_frequency_buffer, outlet.written, available);

		int i;
		for (i = outlet.written; i < outlet.written + available; i++) {
			outlet.audio[0][i] = lnode.curve_wave[Math.floorMod((int) (pos / (Math.PI * 2 * sample_rate) * lnode.curve_wave.length), lnode.curve_wave.length)];
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

		if(active_tangent != null && resampling != null) {
			double freq = midiNoteToFreq(active_tangent[1] + pitch); // TODO take care of pitch wheel
			float volume_div = sample_rate / 1000;
			float[] audio = outlet.audio[0];

			resampling.update((float)freq, available);

			if (volume != new_volume) {
				int start = outlet.written;
				int stop = start + available;
				for (int i = start; i < stop; i++) {
					volume += (new_volume - volume) / volume_div;
					audio[i] *= volume;
				}
			}

		} else { // No key down? Silence!
			pos = 0;
			for(int i = 0; i < buffer_size; i++)
				outlet.audio[0][i] = 0;
		}

		outlet.written += available;
		outlet.push();
	}

	private void updateKeysDown() {
		MidiInlet inlet = (MidiInlet)getInlet("frequency");
		if(inlet.available() > 0) {
			short[][][] midi = ((MidiOutlet)inlet.outlet).midi;

			int packet_sample_offset = 0;
			for(short[][] sample : midi) {
				if(sample != null) {
					for(short[] midi_packet : sample) {
						if((midi_packet[0] & 0b11110000) == MidiStatuses.KEY_DOWN) {
							if(!sustain)
								keys_down.add(midi_packet);

							active_tangent = midi_packet;
						} else if((midi_packet[0] & 0b11110000) == MidiStatuses.KEY_UP) { // Also detect KEY_DOWN with 0 velocity!
							Iterator<short[]> iter = keys_down.iterator();
							while(iter.hasNext()) {
								short[] m = iter.next();
								if(m[1] == midi_packet[1])
									iter.remove();
							}
							if(!sustain)
								active_tangent = keys_down.isEmpty() ? null : keys_down.get(keys_down.size() - 1);
						} else if((midi_packet[0] & 0b11110000) == MidiStatuses.MOD_CHANGE && midi_packet[1] == MidiControllers.SUSTAIN) {
							if(sustain && midi_packet[2] == 0) {
								if(keys_down.isEmpty())
									active_tangent = null;
								else
									active_tangent = keys_down.get(keys_down.size() - 1);
							}

							sustain = midi_packet[2] != 0;
						} else if((midi_packet[0] & 0b11110000) == MidiStatuses.MOD_CHANGE && midi_packet[1] == MidiControllers.VOLUME) {
							new_volume = midi_packet[2] / 127f; // TODO interpolate! plz
							if(packet_sample_offset == 0) // We forcefully set our volume, with no interpolation, if this is the first sample. ADSR outputs the volume in the same sample as the KEY_DOWN
								volume = new_volume;
						} else if((midi_packet[0] & 0b11110000) == MidiStatuses.PITCH_CHANGE) {
							pitch = midi_packet[2] / 32f - 2f;
						}
					}
				}
				packet_sample_offset++;
			}

			inlet.read += inlet.available();
		}
	}

	private double midiNoteToFreq(float n) {
		return 440 * Math.pow(2, (n - 69) / 12.0f);
	}

	@Override
	protected void onPrepare() {
		if(resampling == null && lnode.resamplingFactory != null && getOutlet("output") != null) {
			AudioOutlet outlet = (AudioOutlet)getOutlet("output");
			resampling = lnode.resamplingFactory.create(outlet.audio[0]);
		}

		if(resampling != null)
			resampling.rewind();
	}

	@Override
	protected void onMessage(Message message) {}

	@Override
	protected void onDestroy() {}

	@Override
	public boolean isKeepingSessionAlive() {
		return active_tangent != null;
	}
}