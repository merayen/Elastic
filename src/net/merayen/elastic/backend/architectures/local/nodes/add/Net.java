package net.merayen.elastic.backend.architectures.local.nodes.add;

import net.merayen.elastic.netlist.*;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.DataRequest;
import net.merayen.elastic.netlist.util.AudioNode;

public class Net extends AudioNode<Processor> {
	public Net(NetList supervisor) {
		super(supervisor, Processor.class);
	}

	protected void onReceive(String port_name, DataPacket dp) {
		super.onReceive(port_name, dp);
		if(port_name.equals("output")) {
			if(dp instanceof DataRequest) { // Forward requests
				request("input_a", (DataRequest)dp, true);
				request("input_b", (DataRequest)dp, false);
			}
		}
	}
}
