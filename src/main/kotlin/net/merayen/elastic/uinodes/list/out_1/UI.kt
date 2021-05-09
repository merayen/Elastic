package net.merayen.elastic.uinodes.list.out_1

import net.merayen.elastic.backend.logicnodes.list.out_1.OutNodeStatisticsMessage
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private val vu = VU()

	override fun onInit() {
		super.onInit()
		layoutWidth = 100f
		layoutHeight = 40f
		titlebar.title = "Out"

		vu.translation.x = 10f
		vu.translation.y = 20f
		add(vu)
	}

	override fun onUpdate() {
		super.onUpdate()

		layoutHeight = 40 + vu.layoutHeight
	}

	override fun onCreatePort(port: UIPort) {
		if(port.name == "in")
			port.translation.y = 20f
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onProperties(properties: BaseNodeProperties) {}

	override fun onData(message: NodeDataMessage) {
		if (message is OutNodeStatisticsMessage) {
			vu.updateVU(message.amplitudes)
			vu.updateOffset(message.offsets)
		}
	}
}
