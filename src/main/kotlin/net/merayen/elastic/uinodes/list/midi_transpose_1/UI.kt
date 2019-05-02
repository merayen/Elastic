package net.merayen.elastic.uinodes.list.midi_transpose_1

import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodeParameterMessage
import net.merayen.elastic.ui.objects.components.ParameterSlider
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.roundToInt

class UI : UINode() {
	private val toneSlider = ParameterSlider()
	private val fineToneSlider = ParameterSlider()

	init {
		layoutWidth = 105f
		layoutHeight = 80f

		toneSlider.setHandler(object : ParameterSlider.IHandler {
			override fun onChange(value: Double, programatic: Boolean) {
				sendParameter("transpose", ((value * 48) - 24).roundToInt())
			}

			override fun onButton(offset: Int) {
				toneSlider.value = (toneSlider.value * 48 + offset).roundToInt() / 48.0
			}

			override fun onLabelUpdate(value: Double): String {
				return "${(value * 48).roundToInt() - 24}"
			}
		})

		fineToneSlider.setHandler(object : ParameterSlider.IHandler {
			override fun onChange(value: Double, programatic: Boolean) {
				sendParameter("transpose", ((value * 48) - 24).roundToInt())
			}

			override fun onButton(offset: Int) {
				fineToneSlider.value = (fineToneSlider.value * 48 + offset).roundToInt() / 48.0
			}

			override fun onLabelUpdate(value: Double): String {
				return "${(value * 48).roundToInt() - 24}"
			}
		})
	}

	override fun onCreatePort(port: UIPort) {
		when (port.name) {
			"in" -> port.translation.y = 20f
			"out" -> {
				port.translation.x = layoutWidth
				port.translation.y = 20f
			}
		}
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onInit() {
		super.onInit()

		titlebar.title = "Midi transpose"

		toneSlider.translation.x = 10f
		toneSlider.translation.y = 20f
		add(toneSlider)

		fineToneSlider.translation.x = 10f
		fineToneSlider.translation.y = 50f
		add(fineToneSlider)
	}

	override fun onMessage(message: NodeParameterMessage) {}
	override fun onData(message: NodeDataMessage) {}

	override fun onParameter(key: String, value: Any) {
		if (key == "transpose" && value is Number) {
			toneSlider.value = (value.toDouble() + 24) / 48
			fineToneSlider.value = (value.toDouble() + 24) / 48
		}
	}
}