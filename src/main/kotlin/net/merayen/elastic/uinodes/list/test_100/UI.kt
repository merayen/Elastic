package net.merayen.elastic.uinodes.list.test_100

import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.objects.components.InputSignalParameters
import net.merayen.elastic.ui.objects.components.curvebox.SignalBezierCurveBox
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	init {
		layoutWidth = 200f
		layoutHeight = 200f
	}

	override fun onCreatePort(port: UIPort) {
		if (port.name == "input") {
			port.translation.y = 20f
		}
	}

	override fun onInit() {
		super.onInit()
		val bwb = SignalBezierCurveBox()
		bwb.translation.x = 20f
		bwb.translation.y = 30f
		add(bwb)
		bwb.insertPoint(1)

		val isa = InputSignalParameters(this, "test")
		isa.translation.x = 10f
		isa.translation.y = 180f
		add(isa)
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onMessage(message: BaseNodeData) {}

	override fun onData(message: NodeDataMessage) {}

	override fun onParameter(instance: BaseNodeData) {}
}
