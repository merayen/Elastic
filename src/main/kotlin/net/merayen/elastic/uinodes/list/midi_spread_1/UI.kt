package net.merayen.elastic.uinodes.list.midi_spread_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private val spread_width: ParameterSlider

	init {
		val self = this
		spread_width = ParameterSlider()
		spread_width.translation.x = 10f
		spread_width.translation.y = 20f
		spread_width.setHandler(object : ParameterSlider.IHandler {
			override fun onLabelUpdate(value: Double): String {
				return String.format("%.2f", value)
			}

			override fun onChange(value: Double, programatic: Boolean) {
				self.sendParameter("layoutWidth", value.toFloat())
			}

			override fun onButton(offset: Int) {
				spread_width.value = spread_width.value + offset / 50.0
			}
		})

		add(spread_width)
	}

	override fun onInit() {
		super.onInit()
		layoutWidth = 100f
		layoutHeight = 50f
		titlebar.title = "Midi Spread"
	}

	override fun onCreatePort(port: UIPort) {
		if (port.name == "input") {
			port.translation.y = 20f
		}

		if (port.name == "output") {
			port.translation.x = 100f
			port.translation.y = 20f
		}
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onMessage(message: NodeParameterMessage) {}

	override fun onData(message: NodeDataMessage) {}

	override fun onParameter(key: String, value: Any) {
		if (key == "layoutWidth")
			spread_width.value = (value as Number).toFloat().toDouble()
	}
}