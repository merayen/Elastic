package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;
import java.util.List;

import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

class Chain {
	private final List<LocalProcessor> processors;
	private final List<LocalProcessor> update_queue;

	Chain(List<LocalProcessor> processors) {
		this.processors = processors;
		this.update_queue = new ArrayList<>(processors); // Queue all processors for update
	}

	void update() {
		
	}

	void handleMessage(Postmaster.Message message) {
		// TODO handle messages like adding/removing ports, connecting/disconnecting.
		// We are probably not going to handle adding/removal of nodes, as this changes the structure and might need a complete recompile

		if(message instanceof CreateNodePortMessage) {
			CreateNodePortMessage m = (CreateNodePortMessage)message;
			LocalProcessor lp = getProcessor(m.node_id);
			if(lp != null)
				lp.addPort();

		} else if(message instanceof CreateNodeMessage || message instanceof RemoveNodeMessage) {
			throw new RuntimeException("Should not happen");

		}
	}

	private LocalProcessor getProcessor(String node_id) {
		for(LocalProcessor lp : processors)
			if(lp.localnode.getID().equals(node_id))
				return lp;

		return null;
	}
}
