package net.merayen.elastic.backend.architectures.local.nodes.voices;

import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.process.AudioProcessor;

public abstract class Processor extends AudioProcessor {
	public Processor(Node net_node, long session_id) {
		super(net_node, session_id);
	}
}
