package net.merayen.merasynth.client.signalgenerator;

import net.merayen.merasynth.midi.MidiStatuses;
import net.merayen.merasynth.net.util.flow.PortBuffer;
import net.merayen.merasynth.net.util.flow.PortBufferIterator;
import net.merayen.merasynth.net.util.flow.portmanager.ManagedPortState;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.EndSessionResponse;
import net.merayen.merasynth.netlist.datapacket.MidiResponse;
import net.merayen.merasynth.netlist.datapacket.SessionCreatedResponse;
import net.merayen.merasynth.process.AudioProcessor;

/*
 * Makes beeping sounds.
 * TODO We need to rethink about data streams. We need a handler class that can help Processors to know
 * how much they should produce, like remember the amount requested, as incoming data might be much more than
 * we have requested due to ports being split.
 */
public class Processor extends AudioProcessor {
	private float midi_amplitude = 1f;
	private float midi_frequency;

	private final Net net_node;

	private double pos = 0;

	public Processor(Node n, long session_id) {
		super(n, session_id);
		this.net_node = (Net)n;
	}

	@Override
	protected void onCreate() { // TODO only allow creation from frequency port? Hmm
		// We tell right nodes that we have created this session and that they should now request from us
		respond("output", new SessionCreatedResponse());

		// TODO Request session on amplitude-port, if it is connected?
	}

	private void tryToGenerate() {
		boolean frequency_connected = net_node.isConnected("frequency");
		boolean amplitude_connected = net_node.isConnected("amplitude");
		ManagedPortState frequency_state = getPortState("frequency");

		float[] r = null;
		if(frequency_connected && frequency_state != null && frequency_state.format == MidiResponse.class)
			r = generateWithMidi();
		else if(!frequency_connected && !amplitude_connected)
			r = generateStandalone();

		if(r != null) {
			AudioResponse ar = new AudioResponse();
			ar.sample_count = r.length;
			ar.channels = new short[]{0}; // We only send mono
			ar.samples = r;
			send("output", ar);
		}
	}

	/**
	 * Generate sine wave without the frequency port or amplitude port.
	 * We then generate data synchronously 
	 */
	private float[] generateStandalone() {
		// Figure how much data we need to generate
		int to_generate = this.getPortBuffer("output").available();
		//for(DataPacket dp : this.getPortBuffer("output"))
		//	if(dp instanceof DataRequest)
		//		to_generate += dp.sample_count;
		//	else
		//		throw new RuntimeException(String.format("Got unexpected packet on output-port: %s", dp.getClass().getName()));

		float[] r = new float[to_generate]; // TODO use shared buffer
		float frequency = net_node.frequency;
		float amplitude = net_node.amplitude;// * midi_amplitude;

		if(amplitude > 0)
			for(int i = 0; i < r.length; i++)
				r[i] = (float)Math.sin(pos += (Math.PI * 2.0 * frequency) / net_node.sample_rate) * amplitude * 0.2f;
		else
				pos += (Math.PI * 2.0 * frequency * r.length) / net_node.sample_rate;

		pos %= Math.PI * 2 * 1000;

		this.getPortBuffer("output").clear();

		return r;
	}

	/**
	 * Generate sine wave from frequency port connected to a MIDI source.
	 * Amplitude may or may not be connected.
	 */
	private float[] generateWithMidi() {
		boolean amplitude_connected = net_node.isConnected("amplitude");
		PortBuffer frequency_buffer = this.getPortBuffer("frequency");
		PortBuffer amplitude_buffer = this.getPortBuffer("amplitude");

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
						if(midi[0] == MidiStatuses.KEY_DOWN) {
							setFrequencyFromMidi(midi);
						} else if(midi[0] == MidiStatuses.KEY_UP) {
							end(); // This sends EndSessionHint and EndSessionResponse and then kill ourself
							throw new PortBufferIterator.Stop();
						}
					}
				}

				if(amplitude_connected) {
					/*float[] f = ((AudioResponse)port_packets[2]).samples;

					for(int i = 0; i < sample_count; i++)
						output[offset + i] =
							a[packet_offsets[0] + i]  * f[packet_offsets[2] + i] + // TODO retrieve the fac_value from fac-port, if connected
							b[packet_offsets[1] + i]  * (1 - f[packet_offsets[2] + i]);*/
					// TODO
					throw new RuntimeException("Not implemented");
				} else {
					for(int i = 0; i < sample_count; i++)
						output[offset + i] = (float)Math.sin(pos += (Math.PI * 2.0 * midi_frequency) / net_node.sample_rate) * midi_amplitude * 0.2f;
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
	}

	private void setFrequencyFromMidi(short[] packet) {
		midi_frequency = (float)(440 * Math.pow(2, (packet[1] - 69) / 12.0f));
		midi_amplitude = packet[2] / 128f;
		pos = 0;
		System.out.println(midi_frequency);
	}

	@Override
	public void onDestroy() {
		//if(!dead)
		//	send("output", new EndSessionResponse());
	}

	private void requestData() {
		PortBuffer output_buffer = getPortBuffer("output");
		int to_request = output_buffer.available();
		if(to_request > 0) {
			if(net_node.isConnected("frequency")) {
				//System.out.printf("Signalgenerator %d requests %d samples\n", this.session_id, to_request);
				this.request("frequency", to_request);
			}

			if(net_node.isConnected("amplitude"))
				this.request("amplitude", to_request);

			output_buffer.clear();
		}
	}

	@Override
	protected void onReceive(String port_name) {
		tryToGenerate();

		if(port_name.equals("output") && (net_node.isConnected("frequency") || net_node.isConnected("amplitude")))
			requestData();
	}

	protected void onReceiveControl(String port_name, DataPacket dp) {
		if(port_name.equals("frequency")) {
			if(dp instanceof EndSessionResponse)
				terminate();
		}
	}
}