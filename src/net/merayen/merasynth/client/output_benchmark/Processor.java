package net.merayen.merasynth.client.output_benchmark;

import net.merayen.merasynth.net.util.flow.portmanager.ProcessorManagedPort;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.EndSessionResponse;
import net.merayen.merasynth.process.AudioProcessor;

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

	public void requestData(int sample_count) {
		int to_request = sample_count - samples_requested;
		if(to_request > 0) {
			request("input", to_request);
			samples_requested += to_request;
		}
	}

	@Override
	public void onDestroy() {}

	@Override
	protected void onCreate() {}
}
