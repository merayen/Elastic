package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

public class NodeConnectMessage extends NetListMessage {
	public final String node_a;
	public final String port_a;
	public final String node_b;
	public final String port_b;

	public NodeConnectMessage(String node_a, String port_a, String node_b, String port_b) {
		this.node_a = node_a;
		this.port_a = port_a;
		this.node_b = node_b;
		this.port_b = port_b;
	}
}
