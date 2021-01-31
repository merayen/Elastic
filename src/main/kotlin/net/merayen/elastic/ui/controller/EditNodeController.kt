package net.merayen.elastic.ui.controller

import net.merayen.elastic.backend.logicnodes.list.group_1.PlaybackStatusMessage
import net.merayen.elastic.system.intercom.ElasticMessage
import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.ui.objects.top.Top
import net.merayen.elastic.ui.objects.top.views.editview.EditNodeView
import net.merayen.elastic.util.NetListMessages
import java.util.stream.Stream

class EditNodeController(top: Top) : Controller(top) {
	class Hello(val editNodeView: EditNodeView) : ElasticMessage

	override fun onInit() {}

	override fun onMessageFromBackend(message: ElasticMessage) {
		when(message) {
			is NodeMessage ->
				for(view in getViews(EditNodeView::class.java))
					if(view.nodeId == message.nodeId)
						view.receiveMessage(message)
		}
	}

	override fun onMessageFromUI(message: ElasticMessage) {
		when(message) {
			is Hello -> message.editNodeView.init(this)
		}
	}

	/**
	 * Retrieve messages to rebuild a node
	 * TODO Should probably not disassemble the whole NetList when just needing a single node's messages
	 */
	fun getMessages(nodeId: String): Stream<ElasticMessage> = NetListMessages.disassemble(top.netlist).stream().filter {
		it is NodeMessage && it.nodeId == nodeId
	}
}