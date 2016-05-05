package net.merayen.elastic.backend.architectures.local.nodes.output_benchmark;

import net.merayen.elastic.net.util.flow.portmanager.ProcessorManagedPort;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.EndSessionResponse;
import net.merayen.elastic.process.AudioProcessor;

/**
 * Processor for outputting audio.
 * Accumulates audio and then the Net-node will acquire data when needed.
 */
public class Processor extends AudioProcessor {
	private final Net node;
	int samples_requested; // Count of samples requested. Net() uses this to request sessions synchronously
	int samples_received;

	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
		this.node = (Net)net_node;
	}

	@Override
	protected void onReceive(String port_name) {
		ProcessorManagedPort input_port = ports.get(port_name);
		if(!input_port.buffer.isEmpty()) {
			node.notifyReceived();
			int a = input_port.buffer.available();
			samples_received += a;
			samples_requested -= a;
		}
		input_port.buffer.clear();
	}

	@Override
	protected void onReceiveControl(String port_name, DataPacket dp) {
		if(port_name.equals("input")) {
			if(dp instanceof EndSessionResponse)
				terminate();
		}
	}

	@Override
	public void onDestroy() {}

	@Override
	protected void onCreate() {}
}
