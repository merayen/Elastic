package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

/**
 * Sent from UI when wanting to change a node parameter.
 * Sent from backend when wanting to change a node paremeter.
 * The other party is always forwarded the message to be informed about the change.
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
