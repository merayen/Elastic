package net.merayen.elastic.uinodes.list.out_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override fun onInit() {
		super.onInit()
		layoutWidth = 100f
		layoutHeight = 40f
		titlebar.title = "Out"
	}

	override fun onCreatePort(port: UIPort) {
		if(port.name == "in")
			port.translation.y = 20f
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: BaseNodeProperties) {}
	override fun onData(message: NodeDataMessage) {}
}
