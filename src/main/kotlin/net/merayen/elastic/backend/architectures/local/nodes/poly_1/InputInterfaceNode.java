package net.merayen.elastic.backend.architectures.local.nodes.poly_1;

import net.merayen.elastic.backend.architectures.local.lets.Outlet;

public interface InputInterfaceNode extends InterfaceNode {
	void setForwardOutlet(int session_id, Outlet outlet);
	void scheduleInterfaceProcessor(int session_id);
}
