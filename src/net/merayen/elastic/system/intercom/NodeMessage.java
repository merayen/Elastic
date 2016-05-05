package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

/**
 * A message some node sends.
 * This is not related to change of parameter.
 */
public class NodeMessage extends Postmaster.Message {
	public final String node_id;
	public final String key; // Parameter identifier for the node 
	public final Object value;

	public NodeMessage(String node_id, String key, String value) {
		super(1000); // 1 second timeout. Loss of message is possible if someone lags
		this.node_id = node_id;
		this.key = key;
		this.value = value;
	}
}
