package net.merayen.elastic.uinodes.list.arithmetic_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override var layoutWidth = 100f
	override var layoutHeight = 60f

	override fun onCreatePort(port: UIPort) {
		when (port.name) {
			"a" -> port.translation.y = 20f
			"b" -> port.translation.y = 40f;
			"out" -> {
				port.translation.x = layoutWidth
				port.translation.y = 20f
			}
		}
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onProperties(properties: BaseNodeProperties) {}
	override fun onData(message: NodeDataMessage) {}
}