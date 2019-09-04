package net.merayen.elastic.system.intercom;

import net.merayen.elastic.backend.nodes.BaseNodeProperties;
import org.jetbrains.annotations.NotNull;

/**
 * Sent between UI, LogicNode and the process backend.
 * Parameters represents the state of nodes and is stored and is guaranteed to be loaded into every node.
 * See NodeDataMessage() to send data that is not a part of the state of the Node.
 * TODO rename to NodePropertiesData
 */
public class NodePropertyMessage implements NodeMessage {
	public final @NotNull String node_id;
	public final BaseNodeProperties instance; // Must be compatible for JSON serializing

	public NodePropertyMessage(@NotNull String node_id, BaseNodeProperties instance) {
		this.node_id = node_id;
		this.instance = instance;
	}

	public String toString() {
		String v = instance.toString().substring(0, Math.min(instance.toString().length(), 100));
		return super.toString() + String.format(" (nodeId=%s, instance=%s)", node_id, v);
	}

	@Override
	public @NotNull String getNodeId() {
		return node_id;
	}
}
