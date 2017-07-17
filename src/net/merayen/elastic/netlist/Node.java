package net.merayen.elastic.netlist;

import java.util.HashMap;
import java.util.Map;

public final class Node extends NetListObject {
	final String id;

	final Map<String,Port> ports = new HashMap<>();

	Node(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public Node copy() {
		Node n = new Node(id);

		for(String p : ports.keySet())
			n.ports.put(p, ports.get(p).copy());

		n.properties.putAll(properties);
		return n;
	}
}
