package net.merayen.elastic.uinodes.list.cutoff_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {

	init {
		layoutWidth = 200f
		layoutHeight = 150f
	}

	override fun onCreatePort(port: UIPort) {
		when (port.name) {
			"in" -> {
				port.translation.y = 20f
			}
			"out" -> {
				port.translation.x = layoutWidth
				port.translation.y = 20f
			}
			"frequency" -> {
				port.translation.y = 40f
			}
			"damping" -> {
				port.translation.y = 60f
			}
		}
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: BaseNodeProperties) {}
	override fun onData(message: NodeDataMessage) {}
	override fun onParameter(instance: BaseNodeProperties) {}
}