package net.merayen.elastic.ui.controller

import net.merayen.elastic.netlist.NetList
import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage
import net.merayen.elastic.system.intercom.backend.ImportFileIntoNodeGroupMessage
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView
import java.util.*

/**
 * Handles messages sent and received by nodes.
 */
class NodeViewController internal constructor(top: Top) : Controller(top) {
	var topNodeId: String? = null
		private set // The topmost node. Automatically figured out upon restoration.

	/**
	 * NetList accumulated based on all the incoming messages.
	 * We can then resend messages when requested.
	 */
	val netList: NetList
		get() = top.netlist

	private val nodeViews: List<NodeView>
		get() {
			val result = ArrayList<NodeView>()

			for (view in getViews(NodeView::class.java)) {
				view.nodeViewController = this
				result.add(view)
			}

			return result
		}

	override fun onInit() {}

	override fun onMessageFromBackend(message: ElasticMessage) {
		if (message is CreateNodeMessage && topNodeId == null)
			topNodeId = message.nodeId

		for (nv in nodeViews)
			nv.handleMessage(message)
	}

	override fun onMessageFromUI(message: ElasticMessage) {
		when (message) {
			is NodePropertyMessage -> {
				if (message.instance.uiTranslation != null)
					for (nv in nodeViews)
						nv.handleMessage(message) // Forward messages with parameters used by us (only)

				sendToBackend(message) // Forward message to backend
			}
			is RemoveNodeMessage,
			is CreateNodeMessage,
			is CreateCheckpointMessage,
			is NodeDataMessage,
			is NodeConnectMessage,
			is NodeDisconnectMessage,
			is ImportFileIntoNodeGroupMessage -> sendToBackend(message)
		}
	}
}
