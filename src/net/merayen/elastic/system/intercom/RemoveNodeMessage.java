package net.merayen.elastic.system.intercom;

public class RemoveNodeMessage extends NetListMessage implements NodeMessage {
	public final String node_id;

	public RemoveNodeMessage(String node_id) {
		this.node_id = node_id;
	}

	@Override
	public String getNodeId() {
		return node_id;
	}
}
