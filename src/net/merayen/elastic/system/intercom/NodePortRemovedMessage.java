package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

public class NodePortRemovedMessage extends Postmaster.Message {
	public final String node_id;
	public final String port;

	public NodePortRemovedMessage(String node_id, String port) {
		this.node_id = node_id;
		this.port = port;
	}
}
