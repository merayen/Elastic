package net.merayen.elastic.system.intercom;

import java.util.HashMap;
import java.util.Map;

import net.merayen.elastic.util.Postmaster;

/**
 * Similar to NodeParameterMessage(), but does not set any state of the Node, only pure data (like audio, FFT results etc).
 * This is also by no way serialized and stored.
 */
public class NodeDataMessage extends Postmaster.Message {
	public final String node_id; 
	public final String key;
	public final Object value; // Must be compatible for JSON serializing

	public NodeDataMessage(String node_id, String key, Object value/*Map<String, Object> value*/) {
		this.node_id = node_id;
		this.key = key;
		this.value = value;
	}
}
