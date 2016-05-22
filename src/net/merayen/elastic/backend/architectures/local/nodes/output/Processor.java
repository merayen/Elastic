package net.merayen.elastic.backend.architectures.local.nodes.output;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.netlist.Node;

/**
 * Processor for outputting audio.
 * Accumulates audio and then the Net-node will acquire data when needed.
 */
public class Processor extends LocalProcessor {
	private boolean valid = true;

	@Override
	protected void onProcess() {
		// TODO output directly to interface
	}
	@Override
	protected void onInit() {
		// TODO Auto-generated method stub
		
	}
}
