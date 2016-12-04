package net.merayen.elastic.system.intercom.coop;

import net.merayen.elastic.util.pack.PackDict;
import net.merayen.elastic.util.pack.PackString;

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
	public PackDict dump() {
		PackDict result = new PackDict();

		result.data.put("user_id", new PackString(user_id));
		result.data.put("node_id", new PackString(node_id));

		return result;
	}
}
