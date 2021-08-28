package net.merayen.elastic.system.intercom;

import org.jetbrains.annotations.NotNull;

public class RemoveNodePortMessage extends NetListMessage implements NodeMessage {
	public final String node_id;
	public final String port;

	public RemoveNodePortMessage(String node_id, String port) {
		this.node_id = node_id;
		this.port = port;
	}

	public String toString() {
		return super.toString() + String.format(" (nodeId=%s, port=%s)", node_id, port);
	}

	@Override
	public @NotNull String getNodeId() {
		return node_id;
	}
}
