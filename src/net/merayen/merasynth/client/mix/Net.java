package net.merayen.merasynth.client.mix;

import net.merayen.merasynth.netlist.*;
import net.merayen.merasynth.netlist.datapacket.AllowNewSessionsRequest;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.netlist.util.AudioNode;

public class Net extends AudioNode<Processor> {
	float fac_value = 0.5f; // TODO Get this from UINode

	public Net(Supervisor supervisor) {
		super(supervisor, Processor.class);
	}

	protected void onReceive(String port_name, DataPacket dp) {
		super.onReceive(port_name, dp);
		if(port_name.equals("output")) {
			if(dp instanceof AllowNewSessionsRequest) {
				send("input_a", new AllowNewSessionsRequest());
				send("input_b", new AllowNewSessionsRequest());
			}
		} else {
			int i = 6;
		}
	}
}
