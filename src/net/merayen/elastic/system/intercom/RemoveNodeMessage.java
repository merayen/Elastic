package net.merayen.elastic.system.intercom;

public class RemoveNodeMessage extends NetListMessage {
	public final String node_id;

	public RemoveNodeMessage(String node_id) {
		this.node_id = node_id;
	}
}
