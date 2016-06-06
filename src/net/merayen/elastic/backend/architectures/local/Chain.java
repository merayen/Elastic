package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.system.intercom.*;
import net.merayen.elastic.util.Postmaster;

class Chain {
	private final LocalProcessor[] processors;

	Chain(LocalProcessor[] processors) {
		this.processors = processors;
	}

	synchronized void update() {
		long watchdog = System.currentTimeMillis() + 10;
		while(watchdog > System.currentTimeMillis()) {
			int processed = 0;

			for(LocalProcessor lp : processors) {
				lp.onProcess();
				processed++;
			}

			if(processed == 0)
				break; // Finished
		}
	}

	synchronized void handleMessage(Postmaster.Message message) {
		// We are probably not going to handle adding/removal of nodes, as this changes the structure and might need a complete recompile

		if(message instanceof CreateNodePortMessage) {
			return; // Ignore. Will be rebuilt on any connection/disconnect

		} else if(message instanceof CreateNodeMessage || message instanceof RemoveNodeMessage) {
			throw new RuntimeException("Should not happen. Chain should have been destroyed and rebuilt from scratch");

		} else if(message instanceof NodeParameterMessage) {
			NodeParameterMessage m = (NodeParameterMessage)message;

			LocalProcessor lp = getProcessor(m.node_id);
			if(lp != null) {
				lp.onMessage(m);
			}
		}
	}

	private LocalProcessor getProcessor(String node_id) {
		for(LocalProcessor lp : processors)
			if(lp.localnode.getID().equals(node_id))
				return lp;

		return null;
	}
}
