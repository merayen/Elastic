package net.merayen.elastic.backend.architectures.local.nodes.adsr;

import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.DataRequest;
import net.merayen.elastic.netlist.datapacket.EndSessionResponse;
import net.merayen.elastic.netlist.datapacket.MidiResponse;
import net.merayen.elastic.netlist.datapacket.SessionCreatedResponse;
import net.merayen.elastic.process.AbstractProcessor;

public class Processor extends AbstractProcessor {
	Net net_node;

	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
		this.net_node = (Net)net_node;
	}

	@Override
	public void handle(String port_name, DataPacket dp) {
		if(port_name.equals("output")) {
			if(dp instanceof DataRequest) {
				requestMidi((DataRequest)dp);
			}
		} else if(port_name.equals("input")) {
			if(dp instanceof SessionCreatedResponse)
				send("output", dp);
			else if(dp instanceof MidiResponse)
				send("output", dp);
			else if(dp instanceof EndSessionResponse)
				terminate();
		}
	}

	@Override
	public void onDestroy() {
		send("output", new EndSessionResponse());
	}

	private void requestMidi(DataRequest mr) {
		send("input", mr);
	}
}
