package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

/**
 * Sent from the backend whenever a node has been created.
 * Whatever receives this one, like the UI, needs to update itself.
 */
public class NodeCreatedMessage extends Postmaster.Message {
	public final String node_id;
	public final String name;
	public final Integer version;

	public NodeCreatedMessage(String node_id, String name, Integer version) {
		this.node_id = node_id;
		this.name = name;
		this.version = version;
	}
}
