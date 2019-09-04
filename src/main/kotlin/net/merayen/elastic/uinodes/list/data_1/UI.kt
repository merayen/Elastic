package net.merayen.elastic.uinodes.list.data_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override fun onCreatePort(port: UIPort) {}
	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: BaseNodeProperties) {}
	override fun onData(message: NodeDataMessage) {}
	override fun onParameter(instance: BaseNodeProperties) {}

	override fun onInit() {
		// Not calling UINode's onInit, to not init the UINode
	}
	override fun onDraw(draw: Draw) {
		// Not calling UINode's onDraw, to hide the node
	}
}