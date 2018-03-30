package net.merayen.elastic.uinodes.list.output_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {

	private val vu = VU()

	init {
		layoutWidth = 120f
		layoutHeight = 100f

		titlebar.title = "Speaker"
	}

	override fun onInit() {
		super.onInit()

		vu.translation.x = 10f
		vu.translation.y = 20f
		add(vu)
	}

	override fun onUpdate() {
		super.onUpdate()

		layoutHeight = 40 + vu.layoutHeight
	}

	override fun onCreatePort(port: UIPort) {
		if (port.name == "input")
			port.translation.y = 20f
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onMessage(message: NodeParameterMessage) {}

	override fun onData(message: NodeDataMessage) {
		if (message.value.containsKey("vu"))
			vu.updateVU(message.value["vu"] as FloatArray)

		else if (message.value.containsKey("offset"))
			vu.updateOffset(message.value["offset"] as FloatArray)
	}

	override fun onParameter(key: String, value: Any) {}
}
