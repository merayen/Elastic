package net.merayen.merasynth.client.adsr;

import net.merayen.merasynth.netlist.Node;
import net.merayen.merasynth.netlist.datapacket.DataPacket;
import net.merayen.merasynth.process.AbstractProcessor;

public class Processor extends AbstractProcessor {
	Net net_node;

	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
		this.net_node = (Net)net_node;
	}

	@Override
	public void handle(String port_name, DataPacket dp) {

	}

	@Override
	public void onDestroy() {

	}

}
