package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

/**
 * A message some node sends.
 * This is not related to change of parameter and will not be reflected in the NetList.
 * Will of course not be dumped and restored either.
 * 
 * Content of this message could be like FFT analysis, spectrum etc.
 */
public class NodeMessage extends Postmaster.Message {
	public final String node_id;
	public final String key; // Parameter identifier for the node 
	public final Object value;

	public NodeMessage(String node_id, String key, String value, boolean important) {
		super(important ? Long.MAX_VALUE : 1000); // 1 second timeout if not important. Messages gets discarded in 1 second if not important

		this.node_id = node_id;
		this.key = key;
		this.value = value;
	}

	public NodeMessage(String node_id, String key, String value) {
		super(Long.MAX_VALUE);

		this.node_id = node_id;
		this.key = key;
		this.value = value;
	}
}
