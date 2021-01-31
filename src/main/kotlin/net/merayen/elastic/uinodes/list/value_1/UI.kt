package net.merayen.elastic.uinodes.list.value_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override fun onInit() {
		super.onInit()
		layoutWidth = 100f
	}

	override fun onCreatePort(port: UIPort) {
		port.translation.x = layoutWidth
		port.translation.y = ports.size * 20f
		layoutHeight = 40f + ports.size * 20f
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onProperties(properties: BaseNodeProperties) {}
	override fun onData(message: NodeDataMessage) {}
}