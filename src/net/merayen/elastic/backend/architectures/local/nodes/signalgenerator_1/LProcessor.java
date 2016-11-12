package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator_1;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.AudioOutlet;
import net.merayen.elastic.backend.architectures.local.lets.Outlet;
import net.merayen.elastic.backend.midi.MidiStatuses;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.util.Postmaster.Message;

/*
 * Makes beeping sounds.
 * TODO We need to rethink about data streams. We need a handler class that can help Processors to know
 * how much they should produce, like remember the amount requested, as incoming data might be much more than
 * we have requested due to ports being split.
 */
public class LProcessor extends LocalProcessor {

	private float midi_amplitude = 1f;
	private float midi_frequency;
	private float midi_tangent_frequency;
	private float midi_pitch_factor;

	int standalone_to_generate;
	int sample_rate;
	List<Short> keys_down = new ArrayList<>();

	private double pos = 0;

	@Override
	protected void onInit() {
		System.out.println("Signalgenerator LProcessor onInit()");
	}

	/*void tryToGenerate() {
		boolean frequency_connected = getInlet("frequency").isConnected("frequency");
		boolean amplitude_connected = ports.isConnected("amplitude");
		ManagedPortState frequency_state = getPortState("frequency");

		float[] r = null;
		if(!frequency_connected && !amplitude_connected) {
			r = generateStandalone();

		} else if(frequency_connected) {
			if(frequency_state == null)
				return; // frequency port is connected but we have not received anything on it, so we don't know the format. We wait

			if(frequency_state.format == MidiResponse.class)
				r = generateWithMidi();
			if(frequency_state.format == AudioResponse.class)
				r = generateFromFrequency();

		} else if(amplitude_connected) { // Only amplitude-port connected
			r = generateStandalone();
		} else {
			throw new RuntimeException("Not implemented");
		}

		if(r != null) {
			AudioResponse ar = new AudioResponse();
			ar.sample_count = r.length;
			ar.channels = new short[]{0}; // We only send mono
			ar.samples = r;
			send("output", ar);
		}
	}*/

	/**
	 * Generate sine wave without the frequency port.
	 * We then generate data synchronously.
	 * Called by the Net-node. 
	 */
	/*public float[] generateStandalone() {
		if(standalone_to_generate == 0)
			return null;

		boolean amplitude_connected = ports.isConnected("amplitude");

		float[] r = new float[standalone_to_generate]; // TODO use shared buffer
		float frequency = net_node.frequency;
		float amplitude = net_node.amplitude;// * midi_amplitude;

		standalone_to_generate = 0;

		float amplitude_offset = ((Node)net_node).offset;

		if(amplitude_connected) {
			
		} else {
			if(amplitude > 0)
				for(int i = 0; i < r.length; i++)
					r[i] = (float)Math.sin(pos += (Math.PI * 2.0 * frequency) / sample_rate) * amplitude + amplitude_offset;
			else
					pos += (Math.PI * 2.0 * frequency * r.length) / sample_rate;
		}

		pos %= Math.PI * 2 * 1000;

		return r;
	}*/

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

	@Override
	public void onDestroy() {
		//if(!dead)
		//	send("output", new EndSessionResponse());
	}

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

	private int n;

	@Override
	protected void onProcess() {
		Outlet outlet = this.getOutlet("output");
		if(outlet != null) {
			AudioOutlet ao = (AudioOutlet)outlet;
			System.out.println("Signalgenerator LProcessor processing " + outlet);

			for(int i = 0; i < ao.audio.length; i++)
				ao.audio[i] = i + n;

			ao.written += ao.audio.length;

			n++;

			outlet.push();
		}
	}

	@Override
	protected void onPrepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(Message message) {
		// TODO Auto-generated method stub
		
	}
}