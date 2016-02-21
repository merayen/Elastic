package net.merayen.merasynth.client.output;

import net.merayen.merasynth.net.util.flow.PortBuffer;
import net.merayen.merasynth.net.util.flow.portmanager.ProcessorManagedPort;
import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.AudioResponse;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.datapacket.EndSessionResponse;
import net.merayen.merasynth.process.AudioProcessor;

/**
 * Processor for outputting audio.
 * Accumulates audio and then the Net-node will acquire data when needed.
 */
public class Processor extends AudioProcessor {
	private final Net node;
	private boolean valid = true;

	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
		this.node = (Net)net_node;
	}

	@Override
	protected void onReceive(String port_name) {
		ProcessorManagedPort input_port = ports.get(port_name);
		if(valid) {
			if(!input_port.buffer.isEmpty()) {
				if(!(input_port.buffer.getLast() instanceof AudioResponse))
					valid = false;
				else
					node.notifyAudioReceived(session_id);
			}
		}

		if(!valid)
			input_port.buffer.clear();
	}

	@Override
	protected void onReceiveControl(String port_name, DataPacket dp) {
		if(port_name.equals("input")) {
			if(dp instanceof EndSessionResponse)
				terminate();
		}
	}

	public void requestAudio(int sample_count) {
		request("input", sample_count);
	}

	public PortBuffer getBuffer() {
		return ports.get("input").buffer;
	}

	public boolean isValid() {
		return valid;
	}

	@Override
	public void onDestroy() {

	}

	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		
	}
}
