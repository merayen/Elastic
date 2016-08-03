package net.merayen.elastic.backend.architectures.local.nodes.add;

import net.merayen.elastic.net.util.flow.PortBuffer;
import net.merayen.elastic.net.util.flow.PortBufferIterator;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.datapacket.AudioResponse;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.EndSessionResponse;
import net.merayen.elastic.netlist.datapacket.SessionCreatedResponse;
import net.merayen.elastic.process.AudioProcessor;

public class Processor extends AudioProcessor {
	private final Net net_node;

	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
		this.net_node = (Net)net_node;
		send("output", new SessionCreatedResponse());
	}

	@Override
	protected void onReceive(String port_name) {
		if(port_name.equals("input_a") || port_name.equals("input_b") || port_name.equals("fac")) {
			tryToMix();
		}
	}

	@Override
	protected void onReceiveControl(String port_name, DataPacket dp) {
		if(port_name.equals("input_a") || port_name.equals("input_b")) {
			if(dp instanceof EndSessionResponse) {
				send("output", new EndSessionResponse());
				// TODO notify input_a or input_b too
				terminate();
			}
		}
	}

	private void tryToMix() {
		boolean a_connected = ports.isConnected("input_a");
		boolean b_connected = ports.isConnected("input_b");
		boolean fac_connected = ports.isConnected("fac");

		if(!a_connected && !b_connected) // TODO Output silence?
			return;

		PortBuffer a_buffer = getPortBuffer("input_a");
		PortBuffer b_buffer = getPortBuffer("input_b");
		PortBuffer fac_buffer = getPortBuffer("fac");

		int to_mix = Math.min(
			a_connected ? a_buffer.available() : Integer.MAX_VALUE,
			b_connected ? b_buffer.available() : Integer.MAX_VALUE
		);

		if(fac_connected)
			to_mix = Math.min(to_mix, fac_buffer.available());

		if(to_mix > 0) {

			float[] output = new float[to_mix];

			// And now all connection cases
			PortBuffer[] ports;
			PortBufferIterator pbi;
			if(a_connected && b_connected) {
				if(fac_connected)
					ports = new PortBuffer[]{a_buffer, b_buffer, fac_buffer};
				else
					ports = new PortBuffer[]{a_buffer, b_buffer};

				pbi = new PortBufferIterator(ports, new PortBufferIterator.IteratorFunc() {
					@Override
					public void loop(DataPacket[] port_packets, int[] packet_offsets, int offset, int sample_count) {
						float[] a = ((AudioResponse)port_packets[0]).samples;
						float[] b = ((AudioResponse)port_packets[1]).samples;

						if(fac_connected) {
							float[] f = ((AudioResponse)port_packets[2]).samples;

							for(int i = 0; i < sample_count; i++)
								output[offset + i] =
									a[packet_offsets[0] + i]  * f[packet_offsets[2] + i] + // TODO retrieve the fac_value from fac-port, if connected
									b[packet_offsets[1] + i]  * (1 - f[packet_offsets[2] + i]);
						} else {
							for(int i = 0; i < sample_count; i++)
								output[offset + i] =
									a[packet_offsets[0] + i]  * net_node.fac_value + // TODO retrieve the fac_value from fac-port, if connected
									b[packet_offsets[1] + i]  * (1 - net_node.fac_value);
						}
					}
				});
			} else if(a_connected || b_connected) {
				if(fac_connected)
					ports = new PortBuffer[]{a_connected ? a_buffer : b_buffer, fac_buffer};
				else
					ports = new PortBuffer[]{a_connected ? a_buffer : b_buffer};

				pbi = new PortBufferIterator(ports, new PortBufferIterator.IteratorFunc() {
					@Override
					public void loop(DataPacket[] port_packets, int[] packet_offsets, int offset, int sample_count) {
						float[] a_or_b = ((AudioResponse)port_packets[0]).samples;

						if(fac_connected) {
							float[] f = ((AudioResponse)port_packets[1]).samples;

							for(int i = 0; i < sample_count; i++)
								output[offset + i] =
									a_or_b[packet_offsets[0] + i]  * (a_connected ? f[packet_offsets[1] + i] : (1 - f[packet_offsets[1] + i]));
						} else {
							for(int i = 0; i < sample_count; i++)
								output[offset + i] =
									a_or_b[packet_offsets[0] + i]  * (a_connected ? net_node.fac_value : (1 - net_node.fac_value));
						}
					}
				});
			} else {
				throw new RuntimeException("Should not happen");
			}

			pbi.forward();

			// TODO send result
			AudioResponse ar = new AudioResponse();
			ar.sample_count = to_mix;
			ar.samples = output;
			ar.channels = new short[]{0}; // TODO Only does mono for now. Might sound utter horrible if run with multiple channels
			send("output", ar);
		}
	}
}
