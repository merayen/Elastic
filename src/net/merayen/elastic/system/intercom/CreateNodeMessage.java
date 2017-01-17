package net.merayen.elastic.system.intercom;

/**
 * Sent to backend to request creation of this node
 */
public class CreateNodeMessage extends NetListMessage {

	public final String node_id;
	public final String name;
	public final Integer version;
	public final String group;

	/**
	 * Create a new node for the first time.
	 */
	public CreateNodeMessage(String name, Integer version, String group) {
		this.node_id = null;
		this.name = name;
		this.version = version;
		this.group = group;
	}

	/**
	 * Create an exiting node.
	 */
	public CreateNodeMessage(String node_id, String name, Integer version, String group) {
		this.node_id = node_id;
		this.name = name;
		this.version = version;
		this.group = group;
	}

	public String toString() {
		return super.toString() + String.format(" (node_id=%s, name=%s, version=%d, group=%s", node_id, name, version, group);
	}
}
