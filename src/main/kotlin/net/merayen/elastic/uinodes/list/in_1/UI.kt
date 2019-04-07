package net.merayen.elastic.uinodes.list.in_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override fun onInit() {
		super.onInit()
		layoutWidth = 100f
		layoutHeight = 40f
		titlebar.title = "Point"
	}

	override fun onCreatePort(port: UIPort) {
		port.translation.x = 100f
		port.translation.y = 20f
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onMessage(message: NodeParameterMessage) {}

	override fun onData(message: NodeDataMessage) {}

	override fun onParameter(key: String, value: Any) {}
}
