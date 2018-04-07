package net.merayen.elastic.uinodes.list.midi_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.node.Resizable
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private val roll_view = RollView(this)

	override fun onInit() {
		super.onInit()
		layoutWidth = 300f
		layoutHeight = 200f
		titlebar.title = "MIDI Roll"

		add(roll_view)

		add(Resizable(this, object : Resizable.Handler {
			override fun onResize() {
				if (getWidth() < 100) layoutWidth = 100f
				if (getHeight() < 100) layoutHeight = 100f
				if (getWidth() > 1000) layoutWidth = 1000f
				if (getHeight() > 1000) layoutHeight = 1000f

				updateLayout()
			}
		}))

		updateLayout()
	}

	override fun onCreatePort(port: UIPort) {
		if (port.name == "in") {
			port.translation.y = 20f
		} else if (port.name == "out") {
			port.translation.y = 20f
		}
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onMessage(message: NodeParameterMessage) {}

	override fun onData(message: NodeDataMessage) {}

	private fun updateLayout() {
		roll_view.width = getWidth() - 40
		roll_view.height = getHeight() - 25
		getPort("out")?.translation?.x = getWidth()
	}

	override fun onParameter(key: String, value: Any) {}
}
