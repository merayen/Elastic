package net.merayen.elastic.system.intercom;

import org.jetbrains.annotations.NotNull;

public class RemoveNodeMessage extends NetListMessage implements NodeMessage {
	public final String node_id;

	public RemoveNodeMessage(String node_id) {
		this.node_id = node_id;
	}

	@Override
	public @NotNull String getNodeId() {
		return node_id;
	}
}
