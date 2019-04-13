package net.merayen.elastic.ui.objects.nodeeditor

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeMessage
import net.merayen.elastic.ui.UIObject

abstract class NodeEditor(val nodeId: String) : UIObject() {
	var layoutWidth = 0f
	var layoutHeight = 0f
	abstract fun onMessage(message: NodeMessage)

	override fun getWidth() = layoutWidth
	override fun getHeight() = layoutHeight

	protected fun sendData(value: Any) {
		sendMessage(NodeDataMessage(nodeId, value))
	}
}