package net.merayen.elastic.system.intercom.coop;

import java.util.HashMap;
import java.util.Map;

/**
 * When sent from client to server, client asks to unlock the node.
 * When sent from server, it means the node has been unlocked (and can be locked by someone again).
 */
public class UnlockNodeMessage extends CoopMessage {
	public final String node_id; // The node to unlock, or that has been unlocked.

	public UnlockNodeMessage(String node_id) {
		this.node_id = node_id;
	}

	public Map<String, Object> dump() {
		Map<String, Object> result = new HashMap<>();
		result.put("nodeId", node_id);
		return result;
	}
}
