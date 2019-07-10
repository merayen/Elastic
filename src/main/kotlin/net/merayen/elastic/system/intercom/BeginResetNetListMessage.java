package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

/**
 * Send to UI or backend to reset everything.
 * Any NetList or states are cleared.
 * 
 * You will need to send messages again to rebuild the state in UI and/or backend afterwards.
 */
public class BeginResetNetListMessage {
	public final String parent_node_id; // Which group to reset. null if everything

	/**
	 * Reset all groups.
	 */
	public BeginResetNetListMessage() {
		parent_node_id = null;
	}

	/**
	 * Reset just one group.
	 */
	public BeginResetNetListMessage(String parent_node_id) {
		this.parent_node_id = parent_node_id;
	}
}
