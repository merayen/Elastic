package net.merayen.elastic.uinodes.list.group_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	val parameters = HashMap<String, Any>()

	override fun onCreatePort(port: UIPort) {}
	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: NodeParameterMessage) {}
	override fun onData(message: NodeDataMessage) {}

	override fun onParameter(key: String, value: Any) {
		parameters[key] = value
	}
}
