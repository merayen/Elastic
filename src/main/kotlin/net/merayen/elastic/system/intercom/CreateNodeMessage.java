package net.merayen.elastic.system.intercom;

import org.jetbrains.annotations.NotNull;

/**
 * Sent to backend to request creation of this node
 */
public class CreateNodeMessage extends NetListMessage implements NodeMessage {
	public final String node_id;
	public final String name;
	public final Integer version;
	public final String parent;

	/**
	 * Create an exiting node.
	 */
	public CreateNodeMessage(String node_id, String name, Integer version, String parent) {
		this.node_id = node_id;
		this.name = name;
		this.version = version;
		this.parent = parent;
	}

	public CreateNodeMessage(String node_id, String name, String parent) {
		this.node_id = node_id;
		this.name = name;
		this.version = 1;
		this.parent = parent;
	}

	public String toString() {
		return super.toString() + String.format(" (nodeId=%s, name=%s, version=%d, parent=%s)", node_id, name, version, parent);
	}

	@Override
	public @NotNull String getNodeId() {
		return node_id;
	}
}
