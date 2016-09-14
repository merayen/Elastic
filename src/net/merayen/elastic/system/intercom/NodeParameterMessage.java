package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

/**
 * Sent from between UI, LogicNode and the backend.
 * Parameters represents the state of nodes and is stored and is guaranteed to be loaded into every node.
 * See NodeDataMessage() to send data that is not a part of the state of the Node.
 */
public class NodeParameterMessage extends Postmaster.Message {
	public final String node_id;
	public final String key; // Parameter identifier for the node 
	public final Object value; // Must be compatible for JSON serializing

	public NodeParameterMessage(String node_id, String key, Object value) {
		this.node_id = node_id;
		this.key = key;
		this.value = value;
	}
}
