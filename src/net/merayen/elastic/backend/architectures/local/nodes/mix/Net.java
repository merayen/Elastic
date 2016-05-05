package net.merayen.elastic.backend.architectures.local.nodes.mix;

import net.merayen.elastic.netlist.*;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.DataRequest;
import net.merayen.elastic.netlist.util.AudioNode;

public class Net extends AudioNode<Processor> {
	float fac_value = 0.5f; // TODO Get this from UINode

	public Net(NetList supervisor) {
		super(supervisor, Processor.class);
	}

	protected void onReceive(String port_name, DataPacket dp) {
		super.onReceive(port_name, dp);
		if(port_name.equals("output")) {
			if(dp instanceof DataRequest) { // Forward requests
				DataRequest dr = (DataRequest)dp;
				if(isConnected("input_a")) {
					request("input_a", dr, true);
					request("input_b", dr, false);
				} else {
					request("input_a", dr, false);
					request("input_b", dr, true);
				}

				request("fac", dr, false);
			}
		}
	}
}
