package net.merayen.elastic.backend.architectures.local;

import net.merayen.elastic.netlist.Node;

public class LocalNodeProperties {
	//private final NetList netlist;

	public LocalNodeProperties(/*NetList netlist*/) {
		//this.netlist = netlist;
	}

	public void setLocalNode(Node node, LocalNode localnode) {
		node.properties.put("architecture.local.localnode", localnode);
	}

	public LocalNode getLocalNode(Node node) {
		return (LocalNode)node.properties.get("architecture.local.localnode");
	}
}
