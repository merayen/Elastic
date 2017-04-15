package net.merayen.elastic.system.intercom.coop;

import java.util.HashMap;
import java.util.Map;

/**
 * When sent to the server, requests to lock a node to the sender.
 * When sent from the server, it is a acknowledge that it got locked.
 */
public class LockNodeMessage extends CoopMessage {
	public final String user_id; // The user that tries to lock or has successfully locked the node. If null, then lock was not approved by the server.
	public final String node_id; // The node to lock

	public LockNodeMessage(String user_id, String node_id) {
		this.user_id = user_id;
		this.node_id = node_id;
	}

	@Override
	public Map<String, Object> dump() {
		Map<String, Object> result = new HashMap<>();

		result.put("user_id", user_id);
		result.put("node_id", node_id);

		return result;
	}
}
