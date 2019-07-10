package net.merayen.elastic.ui.controller

import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.ui.objects.top.views.editview.EditNodeView
import net.merayen.elastic.util.NetListMessages
import java.util.stream.Stream

class EditNodeController(gate: Gate) : Controller(gate) {
	class Hello(val editNodeView: EditNodeView)

	override fun onInit() {}

	override fun onMessageFromBackend(message: Any) {
		when(message) {
			is NodeMessage ->
				for(view in getViews(EditNodeView::class.java))
					if(view.nodeId == message.nodeId)
						view.receiveMessage(message)
		}
	}

	override fun onMessageFromUI(message: Any) {
		when(message) {
			is Hello ->
				message.editNodeView.init(this)
		}
	}

	/**
	 * Retrieve messages to rebuild a node
	 * TODO Should probably not disassemble the whole NetList when just needing a single node's messages
	 */
	fun getMessages(nodeId: String): Stream<Any> = NetListMessages.disassemble(gate.netlist).stream().filter {
		it is NodeMessage && it.nodeId == nodeId
	}

	fun getNodeProperties(nodeId: String) = gate.netlist.getNode(nodeId)?.properties
}