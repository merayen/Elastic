package net.merayen.elastic.backend.architectures.local.nodes.signalgenerator;

import net.merayen.elastic.backend.architectures.local.LocalNode;
import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.datapacket.DataPacket;
import net.merayen.elastic.netlist.datapacket.DataRequest;
import net.merayen.elastic.netlist.util.AudioNode;

/**
 * TODO change mode when frequency-port is connected/disconnected
 */
public class Node extends LocalNode {
	float frequency = 1000f; // Only used in STANDALONE mode. This parameter is set in the UI
	float amplitude = 1f;  // Only used in STANDALONE mode. This parameter is set in the UI
	float offset;

	public Node() {
		super(Processor.class);
	}

	public double onUpdate() {
		// Doesn't process anything, unless explicitly asked for data
		return DONE;
	}

	@Override
	protected void onInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onProcess() {
		// TODO Auto-generated method stub
		
	}
}
