package net.merayen.elastic.uinodes.list.projectcars2_1

import net.merayen.elastic.backend.logicnodes.list.projectcars2_1.LogicNode.Companion.paramNames
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	override fun onInit() {
		super.onInit()
		layoutWidth = 120f
		layoutHeight = 220f
		titlebar.title = "Project Cars 2 UDP"
	}

	override fun onCreatePort(port: UIPort) {
		port.translation.x = 120f

		val index = paramNames.indexOf(port.name)
		if (index == -1)
			error("Unknown param/port ${port.name}")

		port.translation.y = (20 + (index * 20)).toFloat()
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onProperties(properties: BaseNodeProperties) {}
	override fun onData(message: NodeDataMessage) {}
}