package net.merayen.elastic.backend.nodes;

import net.merayen.elastic.netlist.NetList;
import net.merayen.elastic.netlist.Node;
import net.merayen.elastic.netlist.Port;

public abstract class BaseLogicNode {
	private String id; // Same ID as the one in NetList
	private NetList netlist;
	private Node node;

	/**
	 * Called when this node is created for the first time.
	 * You will need to initialize stuff like defining ports.
	 */
	protected abstract void onCreate();

	protected void definePort(String name, boolean output) {
		Port port = node.createPort(name);
		port.properties.put("output", output);
	}

	void setInfo(String id, NetList netlist, Node node) {
		this.id = id;
		this.netlist = netlist;
		this.node = node;
	}

	public String getID() {
		return id;
	}
}
