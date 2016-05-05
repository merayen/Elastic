package net.merayen.elastic.backend.architectures.local;

/**
 * Very simple, internal netlist for processing.
 */
class LocalNetList {
	LocalNode[] nodes;

	LocalNetList(LocalNode[] nodes) {
		this.nodes = nodes;
	}

	public LocalNode get(String id) {
		for(LocalNode node : nodes)
			if(node.getID().equals(id))
				return node;

		return null;
	}
}
