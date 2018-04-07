package net.merayen.elastic.ui.controller

import java.util.ArrayList

import net.merayen.elastic.system.intercom.*
import net.merayen.elastic.system.intercom.backend.CreateCheckpointMessage
import net.merayen.elastic.ui.objects.top.views.nodeview.NodeView
import net.merayen.elastic.util.NetListMessages
import net.merayen.elastic.util.Postmaster
import net.merayen.elastic.util.Postmaster.Message

/**
 * Handles messages sent and received by nodes.
 */
class NodeViewController internal constructor(gate: Gate) : Controller(gate) {

	/**
	 * NetList accumulated based on all the incoming messages.
	 * We can then resend messages when requested.
	 */
	var topNodeID: String? = null
		private set // The topmost node. Automatically figured out upon restoration.

	private val nodeViews: List<NodeView>
		get() {
			val result = ArrayList<NodeView>()

			for (view in getViews(NodeView::class.java))
				if (view.viewNodeID != null)
					result.add(view)

			return result
		}

	/**
	 * NodeViews send this message when they are created.
	 * These messages get picked up by us and we register them.
	 */
	class Hello : Postmaster.Message()

	override fun onInit() {}

	override fun onMessageFromBackend(message: Postmaster.Message) {
		// Forward message regarding the net, from backend to the UINet, to all NodeViews
		when (message) {
			is CreateNodeMessage -> {
				if (message.parent == null)
					topNodeID = message.node_id // Found the topmost node

				for (nv in nodeViews)
					if (nv.viewNodeID == message.parent)
						nv.addNode(message.node_id, message.name, message.version, message.parent)

			}

			is RemoveNodeMessage -> for (nv in nodeViews)
				if (nv.getNode(message.node_id) != null)
					nv.removeNode(message.node_id) // Exception? UI out of sync

			is BeginResetNetListMessage -> // TODO implement support to only reset a certain group?
				for (nv in nodeViews)
					nv.reset()

			is NodeMessage -> for (nv in nodeViews)
				nv.getNode(message.nodeId)?.executeMessage(message)
		}

		// UINet
		if (message is NodeConnectMessage || message is NodeDisconnectMessage || message is RemoveNodeMessage || message is RemoveNodePortMessage)
			for (nv in nodeViews)
				nv.uiNet.handleMessage(message) // Forward message regarding the net, from backend to the UINet, to all NodeViews
	}

	override fun onMessageFromUI(message: Message) {
		if (message is Hello) {  // Set us on the NodeViews, so that they can call us
			for (view in getViews(NodeView::class.java))
				view.node_view_controller = this

		} else if (message is NodeParameterMessage) {

			if (message.key.startsWith("ui.java."))
				for (nv in nodeViews)
					nv.messageNode(message.node_id, message) // Forward messages with parameters used by us (only)

			sendToBackend(message) // Forward message to backend
		} else if (message is RemoveNodeMessage ||
				message is CreateNodeMessage ||
				message is CreateCheckpointMessage ||
				message is NodeDataMessage ||
				message is NodeConnectMessage ||
				message is NodeDisconnectMessage) {
			sendToBackend(message)
		} else if (message is NetListRefreshRequestMessage) { // Move it out to a separate controller, with only purpose to accumulate the netlist and resend it?
			val messages = ArrayList<Postmaster.Message>()
			messages.add(BeginResetNetListMessage())
			messages.addAll(NetListMessages.disassemble(gate.netlist))
			messages.add(FinishResetNetListMessage())
			for (m in messages)
				onMessageFromBackend(m)
		}
	}
}
