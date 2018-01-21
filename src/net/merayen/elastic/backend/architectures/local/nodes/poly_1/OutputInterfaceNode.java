package net.merayen.elastic.backend.architectures.local.nodes.poly_1;

import net.merayen.elastic.backend.architectures.local.LocalProcessor;
import net.merayen.elastic.backend.architectures.local.lets.Inlet;

public interface OutputInterfaceNode extends InterfaceNode {
	/**
	 * Retrieve out-inlet.
	 */
	Inlet getOutputInlet(int session_id);
}
