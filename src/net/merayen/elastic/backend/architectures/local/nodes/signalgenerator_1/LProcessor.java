package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator_1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MidiFileFormat;

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
		float amplitude = lnode.amplitude;
		for(int i = outlet.written; i < outlet.buffer_size; i++) {
			outlet.audio[0][i] = lnode.curve_wave[(int)((pos / (Math.PI * 2) * lnode.curve_wave.length) % lnode.curve_wave.length)];
			pos += step;
		}

		outlet.written = outlet.buffer_size;
		outlet.push();
	}

	private void generateWithFrequency() {
		//System.out.println(sample_rate + " " + frequency);
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
			float freq = midiNoteToFreq(active_key[1]); // TODO take care of pitch wheel

			for(int i = outlet.written; i < outlet.written + available; i++) {
				outlet.audio[0][i] = lnode.curve_wave[Math.floorMod((int)(pos / (Math.PI * 2 * sample_rate) * lnode.curve_wave.length), lnode.curve_wave.length)];
				pos += freq;
			}
		} else { // No key down? Silence!
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

	/**
	 * Generate sine wave from frequency port connected to a MIDI source.
	 * Amplitude may or may not be connected.
	 */
	/*private float[] generateWithMidi() {
		boolean amplitude_connected = net_node.isConnected("amplitude");
		PortBuffer frequency_buffer = getPortBuffer("frequency");
		PortBuffer amplitude_buffer = getPortBuffer("amplitude");

		int to_generate = Math.min(frequency_buffer.available(), Integer.MAX_VALUE);
		if(amplitude_connected)
			to_generate = Math.min(amplitude_buffer.available(), to_generate);

		if(to_generate == 0)
			return null;

		float[] output = new float[to_generate];

		PortBuffer[] ports;
		if(amplitude_connected)
			ports = new PortBuffer[]{frequency_buffer, amplitude_buffer};
		else
			ports = new PortBuffer[]{frequency_buffer};

		new PortBufferIterator(ports, new PortBufferIterator.IteratorFunc() {

			private MidiResponse last_mr_packet = null;

			@Override
			public void loop(DataPacket[] port_packets, int[] packet_offsets, int offset, int sample_count) {
				// MIDI packet swapped? We update ourself
				if(port_packets[0] != last_mr_packet) {
					last_mr_packet = (MidiResponse)port_packets[0];
					for(short[] midi : last_mr_packet.midi) { // XXX Placing MIDI interpreting here is not 100% accurate. Problem?
						updateFromMidi(midi);
					}
				}

				if(amplitude_connected) {
					/*float[] f = ((AudioResponse)port_packets[2]).samples;

					for(int i = 0; i < sample_count; i++)
						output[offset + i] =
							a[packet_offsets[0] + i]  * f[packet_offsets[2] + i] + // TODO retrieve the fac_value from fac-port, if connected
							b[packet_offsets[1] + i]  * (1 - f[packet_offsets[2] + i]);* /
					// TODO
					throw new RuntimeException("Not implemented");
				} else {
					for(int i = 0; i < sample_count; i++)
						output[offset + i] = (float)Math.sin(pos += (Math.PI * 2.0 * midi_frequency) / sample_rate) * midi_amplitude * net_node.amplitude * 0.2f;
				}
			}
		});

		// Forward the buffer
		if(isAlive()) { // We might kill ourself in the loop above, and since the buffers get cleared, we will fail here
			frequency_buffer.forward(to_generate);
			if(amplitude_connected)
				amplitude_buffer.forward(to_generate);
		}

		return output;
	}*/

	/*private void updateFromMidi(short[] midi) {
		if(midi[0] == MidiStatuses.KEY_DOWN) {
			keys_down.add(midi[1]);
 
			float freq = midiNoteToFreq(midi[1]);
			midi_tangent_frequency = freq;
			midi_amplitude = midi[2] / 128f;
		} else if(midi[0] == MidiStatuses.KEY_UP) {
			if(inPolyMode())
				end();

			// Remove the key from our memory. This is mostly used when in mono-mode
			for(int i = keys_down.size() - 1; i >= 0; i--)
				if(keys_down.get(i) == midi[1])
					keys_down.remove(i);

			if(keys_down.size() > 0) {
				midi_tangent_frequency = midiNoteToFreq(keys_down.get(keys_down.size() - 1)); // Play the previous pushed tangent
			} else {
				midi_tangent_frequency = 0;
				midi_amplitude = 0;
				pos = 0;
			}
		} else if(midi[0] == MidiStatuses.PITCH_CHANGE) {
			midi_pitch_factor = (midi[1] + midi[2] * 128) / 8192f - 1;
			System.out.printf("Pitch factor: %f\n", midi_pitch_factor);
		}

		midi_frequency = (float)(midi_tangent_frequency * Math.pow(2, midi_pitch_factor / 6f));
	}*/

	private float midiNoteToFreq(short n) {
		return (float)(440 * Math.pow(2, (n - 69) / 12.0f));
	}

	/*private float[] generateFromFrequency() {
		boolean amplitude_connected = net_node.isConnected("amplitude");
		PortBuffer frequency_buffer = this.getPortBuffer("frequency");
		PortBuffer amplitude_buffer = this.getPortBuffer("amplitude");

		int to_generate = Math.min(frequency_buffer.available(), Integer.MAX_VALUE);
		if(amplitude_connected)
			to_generate = Math.min(amplitude_buffer.available(), to_generate);

		if(to_generate == 0)
			return null;

		float[] output = new float[to_generate];

		PortBuffer[] ports = amplitude_connected ? new PortBuffer[]{frequency_buffer, amplitude_buffer} : new PortBuffer[]{frequency_buffer};

		float amplitude_offset = ((Node)net_node).offset;

		PortBufferIterator pbi = new PortBufferIterator(ports, new PortBufferIterator.IteratorFunc() {

			@Override
			public void loop(DataPacket[] port_packets, int[] packet_offsets, int offset, int sample_count) {
				if(amplitude_connected) {
					/*float[] f = ((AudioResponse)port_packets[2]).samples;

					for(int i = 0; i < sample_count; i++)
						output[offset + i] =
							a[packet_offsets[0] + i]  * f[packet_offsets[2] + i] + // TODO retrieve the fac_value from fac-port, if connected
							b[packet_offsets[1] + i]  * (1 - f[packet_offsets[2] + i]);* /
					// TODO
					throw new RuntimeException("Not implemented");
				} else {
					float[] f = ((AudioResponse)port_packets[0]).samples;
					for(int i = 0; i < sample_count; i++)
						output[offset + i] = (float)Math.sin(pos += (Math.PI * 2.0 * f[packet_offsets[0] + i]) / sample_rate) * net_node.amplitude + amplitude_offset;
				}
			}
		});

		pbi.forward();

		return output;
	}*/

	/*@Override
	protected void onReceive(String port_name) {
		tryToGenerate();
	}*/

	/*protected void onReceiveControl(String port_name, DataPacket dp) {
		if(port_name.equals("frequency")) {
			if(dp instanceof EndSessionResponse)
				terminate();
		}
	}*/

	/*private int n;

	@Override
	protected void onProcess() {
		Outlet outlet = getOutlet("output");
		Inlet frequency = getInlet("frequency");
		Inlet amplitude = getInlet("amplitude");

		int available = available();

		if(outlet != null && available > 0) {
			AudioOutlet ao = (AudioOutlet)outlet;
			System.out.println("Signalgenerator LProcessor processing " + outlet);

			for(int i = ao.written; i < ao.written + available; i++)
				ao.audio[i] = i + n;

			ao.written += available;

			if(frequency != null)
				frequency.read += available;

			if(amplitude != null)
				amplitude.read += available;

			n++;

			outlet.push();
		}
	}*/

	@Override
	protected void onPrepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}