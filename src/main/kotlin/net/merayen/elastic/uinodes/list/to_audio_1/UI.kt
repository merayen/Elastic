package net.merayen.elastic.uinodes.list.to_audio_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override fun onInit() {
		super.onInit()
		layoutWidth = 100f
		layoutHeight = 50f
	}

	override fun onCreatePort(port: UIPort) {
		port.translation.y = 20f

		if (port.name == "out")
			port.translation.x = layoutWidth
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onProperties(properties: BaseNodeProperties) {}
	override fun onData(message: NodeDataMessage) {}
}