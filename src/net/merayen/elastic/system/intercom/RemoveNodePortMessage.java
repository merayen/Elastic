package net.merayen.elastic.system.intercom;

public class RemoveNodePortMessage extends NetListMessage {
	public final String node_id;
	public final String port;

	public RemoveNodePortMessage(String node_id, String port) {
		this.node_id = node_id;
		this.port = port;
	}

	public String toString() {
		return super.toString() + String.format(" (node_id=%s, port=%s)", node_id, port);
	}
}
