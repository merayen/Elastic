package net.merayen.elastic.uinodes.list.data_1

import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override fun onCreatePort(port: UIPort) {}
	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: BaseNodeData) {}
	override fun onData(message: NodeDataMessage) {}
	override fun onParameter(instance: BaseNodeData) {}

	override fun onInit() {
		// Not calling UINode's onInit, to not init the UINode
	}
	override fun onDraw(draw: Draw) {
		// Not calling UINode's onDraw, to hide the node
	}
}