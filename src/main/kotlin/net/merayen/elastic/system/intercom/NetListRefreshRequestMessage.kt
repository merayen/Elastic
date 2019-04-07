package net.merayen.elastic.system.intercom

import net.merayen.elastic.util.Postmaster

/**
 * UI sends this message when it wants to be restored by the NetList. Backend
 * responds with the ResetNetListMessage() and then sends all the necessary
 * messages to rebuild the UI again.
 */
class NetListRefreshRequestMessage(val group_id: String? = null) : Postmaster.Message()
