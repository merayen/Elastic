package net.merayen.elastic.uinodes.list.out_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override fun onInit() {
		super.onInit()
		width = 100f
		height = 40f
		titlebar.title = "Out"
	}

	override fun onCreatePort(port: UIPort) {
		if(port.name == "in")
			port.translation.y = 20f
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: NodeParameterMessage) {}
	override fun onData(message: NodeDataMessage) {}
	override fun onParameter(key: String, value: Any) {}
}
