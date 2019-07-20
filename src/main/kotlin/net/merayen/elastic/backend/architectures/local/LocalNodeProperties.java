package net.merayen.elastic.backend.architectures.local;

import java.util.ArrayList;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;

public class LocalNodeProperties {
	//private final NetList netlist;

	public LocalNodeProperties(/*NetList netlist*/) {
		//this.netlist = netlist;
	}

	public void setLocalNode(Node node, LocalNode localnode) { // Not working
		//node.properties.put("architecture.local.localnode", localnode);
	}

	public LocalNode getLocalNode(Node node) {
		return (LocalNode)node.properties.get("architecture.local.localnode");
	}

	public void clear(NetList netlist) {
		for(Node n : netlist.getNodes())
			for(String k : new ArrayList<>(n.properties.keySet()))
				if(k.startsWith("architecture.local."))
					n.properties.remove(k);
	}
}
