package net.merayen.elastic.uinodes.list.eq_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.Draw
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private val multiParameterEq = MultiParameterEq()

	override fun onInit() {
		super.onInit()
		layoutWidth = 300f
		layoutHeight = 150f

		this.titlebar.title = "Equalizer"

		multiParameterEq.translation.x = 10f
		multiParameterEq.translation.y = 20f
		add(multiParameterEq)
	}

	override fun onDraw(draw: Draw) {
		super.onDraw(draw)
		multiParameterEq.layoutWidth = layoutWidth - 20
		multiParameterEq.layoutHeight = layoutHeight - 30
	}

	override fun onCreatePort(port: UIPort) {
		if(port.name == "in") {
			port.translation.y = 20f
		}

		if(port.name == "out") {
			port.translation.x = 300f
			port.translation.y = 20f
		}
	}

	override fun onRemovePort(port: UIPort) {}
	override fun onMessage(message: NodeParameterMessage) {}
	override fun onData(message: NodeDataMessage) {}
	override fun onParameter(key: String, value: Any) {}
}