package net.merayen.elastic.system.intercom.coop;

import net.merayen.elastic.util.pack.PackDict;
import net.merayen.elastic.util.pack.PackString;

/**
 * When sent from client to server, client asks to unlock the node.
 * When sent from server, it means the node has been unlocked (and can be locked by someone again).
 */
public class UnlockNodeMessage extends CoopMessage {
	public final String node_id; // The node to unlock, or that has been unlocked.

	public UnlockNodeMessage(String node_id) {
		this.node_id = node_id;
	}

	@Override
	public PackDict dump() {
		PackDict result = new PackDict();
		result.data.put("node_id", new PackString(node_id));
		return null;
	}
}
