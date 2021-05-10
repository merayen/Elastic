package net.merayen.elastic.uinodes.list.projectcars2_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override fun onInit() {
		super.onInit()
		layoutWidth = 120f
		layoutHeight = 200f
	}

	override fun onCreatePort(port: UIPort) {
		port.translation.x = 120f

		when (port.name) {
			"rpm" -> port.translation.y = 20f
			"nm" -> port.translation.y = 40f
			"hp" -> port.translation.y = 60f
			"running" -> port.translation.y = 80f
			"engine_on" -> port.translation.y = 100f
		}
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onProperties(properties: BaseNodeProperties) {}
	override fun onData(message: NodeDataMessage) {}
}