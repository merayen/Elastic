package net.merayen.elastic.system.intercom;

import net.merayen.elastic.util.Postmaster;

/**
 * UI sends this message when it wants to be restored by the NetList. Backend
 * responds with the ResetNetListMessage() and then sends all the necessary
 * messages to rebuild the UI again.
 */
public class NetListRefreshRequestMessage extends Postmaster.Message {
	public final String group_id; // Which group to reset. null if everything

	/**
	 * Request to rebuild everything.
	 */
	public NetListRefreshRequestMessage() {
		group_id = null;
	}

	/**
	 * Request to rebuild only one group.
	 */
	public NetListRefreshRequestMessage(String group_id) {
		this.group_id = group_id;
	}
}
