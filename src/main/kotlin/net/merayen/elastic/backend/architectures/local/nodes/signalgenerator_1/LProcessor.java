package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.*;
import net.merayen.elastic.backend.architectures.local.nodes.poly_1.SessionKeeper;
import net.merayen.elastic.backend.architectures.local.utils.InputSignalParametersProcessor;
import net.merayen.elastic.backend.midi.MidiState;
import net.merayen.elastic.backend.util.AudioUtil;

import java.util.*;

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

	private Mode mode;

	private float[][] input_frequency_buffer;

	private List<short[]> keys_down = new ArrayList<>();

	private boolean sustain;
	private float volume;
	private short[] active_tangent;

	private double pos = new Random().nextDouble() * Math.PI * 2;

	private MidiState midiState = new MidiState() {
		@Override
		protected void onPitchBendSensitivityChange(float v) {}

		@Override
		protected void onKeyDown(short tangent, float _velocity) {
			if (!sustain)
				keys_down.add(getCurrentMidiPacket());

			active_tangent = getCurrentMidiPacket();
		}

		@Override
		protected void onKeyUp(short tangent) {
			keys_down.removeIf(m -> m[1] == tangent);
			if (!sustain)
				active_tangent = keys_down.isEmpty() ? null : keys_down.get(keys_down.size() - 1);
		}

		@Override
		protected void onSustain(float value) {
			/*if (sustain && getCurrentMidiPacket()[2] == 0) {
				if (keys_down.isEmpty())
					active_tangent = null;
				else
					active_tangent = keys_down.get(keys_down.size() - 1);
			}

			sustain = getCurrentMidiPacket()[2] != 0;*/
		}

		@Override
		protected void onVolumeChange(float volume) {}

		@Override
		protected void onPitchChange(float semitones) {
			//System.out.println(getSessionID() + "\t" + semitones);
		}
	};

	@Override
	protected void onInit() {
		lnode = (LNode)getLocalNode();

		Inlet frequency = getInlet("frequency");
		Outlet output = getOutlet("output");

		if(output == null)
			mode = Mode.NOTHING;
		if(frequency == null) {
			mode = Mode.RAW;
		} else {
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
		if (frameFinished())
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

		if(resampling != null && !outlet.available())
			resampling.update(lnode.frequency);

		outlet.push();
	}

	private void generateWithFrequency() {
		if (!available())
			return;

		AudioOutlet outlet = (AudioOutlet) getOutlet("output");
		AudioInlet frequency = (AudioInlet) getInlet("frequency");

		// Transform input according to the UI's InputSignalProcessor() parameters
		if (input_frequency_buffer == null)
			input_frequency_buffer = new float[1][buffer_size];

		InputSignalParametersProcessor.process(lnode, "frequency", new float[][]{frequency.outlet.audio[0]}, input_frequency_buffer, 0, buffer_size);

		int i;
		for (i = 0; i < buffer_size; i++) {
			outlet.audio[0][i] = lnode.curve_wave[Math.floorMod((int) (pos / (Math.PI * 2 * sampleRate) * lnode.curve_wave.length), lnode.curve_wave.length)];
			pos += input_frequency_buffer[0][i];
		}

		outlet.push();
	}

	private void generateWithMidi() {
		if(!available())
			return;

		AudioOutlet outlet = (AudioOutlet)getOutlet("output");
		MidiInlet inlet = (MidiInlet)getInlet("frequency");

		updateKeysDown();

		float pitch = midiState.getPitch();

		if(active_tangent != null && resampling != null) {
			double freq = AudioUtil.midiNoteToFreq(active_tangent[1] + pitch);
			float volume_div = sampleRate / 1000f;
			float[] audio = outlet.audio[0];

			resampling.update((float)freq, buffer_size);
			float new_volume = (float)Math.pow(midiState.getVolume(), 3) * midiState.getVelocity();

			if (volume != new_volume) {
				for (int i = 0; i < buffer_size; i++) {
					volume += (new_volume - volume) / volume_div;
					audio[i] *= volume;
				}
			} else if(volume != 1f) {
				for (int i = 0; i < buffer_size; i++)
					audio[i] *= volume;
			}

		} else { // No key down? Silence!
			pos = new Random().nextDouble() * Math.PI * 2;
			for(int i = 0; i < buffer_size; i++)
				outlet.audio[0][i] = 0;
		}

//		System.out.printf("signalgenerator_1 pushing audio out for session %s (%f)\n", getSessionID(), outlet.audio[0][new Random().nextInt(buffer_size)]);

		outlet.push();
	}

	private void updateKeysDown() {
		MidiInlet inlet = (MidiInlet)getInlet("frequency");
		if(inlet.available()) {
			MidiOutlet.MidiFrame midiFrame;
			for (Map.Entry<Integer, MidiOutlet.MidiFrame> entry : inlet.outlet.midi.entrySet()) {
				for (short[] midiPacket : entry.getValue()) {
					midiState.handle(midiPacket);
				}
			}
		}
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
	protected void onDestroy() {}

	@Override
	public boolean isKeepingSessionAlive() {
		return active_tangent != null;
	}
}