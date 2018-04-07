package net.merayen.elastic.system.intercom

import net.merayen.elastic.util.Postmaster

/**
 * UI sends this message when it wants to be restored by the NetList. Backend
 * responds with the ResetNetListMessage() and then sends all the necessary
 * messages to rebuild the UI again.
 */
class NetListRefreshRequestMessage : Postmaster.Message {
	val group_id: String? // Which group to reset. null if everything

	/**
	 * Request to rebuild everything.
	 */
	constructor() {
		group_id = null
	}

	/**
	 * Request to rebuild only one group.
	 */
	constructor(group_id: String) {
		this.group_id = group_id
	}
}
