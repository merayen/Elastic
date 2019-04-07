package net.merayen.elastic.system.intercom;

public class NodeDisconnectMessage extends NetListMessage {
	public final String node_a;
	public final String port_a;
	public final String node_b;
	public final String port_b;

	public NodeDisconnectMessage(String node_a, String port_a, String node_b, String port_b) {
		this.node_a = node_a;
		this.port_a = port_a;
		this.node_b = node_b;
		this.port_b = port_b;
	}

	public String toString() {
		return super.toString() + String.format(" (node_a=%s, port_a=%s, node_b=%s, port_b=%s)", node_a, port_a, node_b, port_b);
	}
}
