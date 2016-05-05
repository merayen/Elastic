package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

public class RemoveNodeMessage extends Postmaster.Message {
	public final String node_id;

	public RemoveNodeMessage(String node_id) {
		this.node_id = node_id;
	}
}
