package net.merayen.elastic.netlist;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Node extends NetListObject {
	final String id;

	final Map<String,Port> ports = new HashMap<>();

	Node(String id) {
		this.id = id;
	}

	/*public Port createPort(String name) {
		Port p = new Port();
		ports.put(name, p);
		return p;
	}*/

	/*public void removePort(String name) {
		ports.remove(name);
	}

	public Port getPort(String name) {
		return ports.get(name);
	}

	**
	 * Returns a list of strings of all the ports this node has.
	 *
	public String[] getPorts() {
		return ports.keySet().toArray(new String[0]);
	}*/

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
