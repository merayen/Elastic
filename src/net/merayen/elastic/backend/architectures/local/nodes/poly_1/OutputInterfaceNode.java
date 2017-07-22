package net.merayen.elastic.backend.architectures.local.nodes.poly_1;

import net.merayen.elastic.backend.architectures.local.lets.Inlet;

public interface OutputInterfaceNode extends InterfaceNode {
	public Inlet getForwardInlet(int session_id);
}
