package net.merayen.elastic.uinodes.list.midi_transpose_1

import net.merayen.elastic.backend.logicnodes.list.midi_transpose_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
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
				sendTransposeParameters()
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
				//sendParameter(((value * 48) - 24).roundToInt())
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

	override fun onData(message: NodeDataMessage) {}

	override fun onProperties(instance: BaseNodeProperties) {
		val data = instance as Properties
		val transposeData = data.transpose
		if (transposeData != null)
			toneSlider.value = (transposeData.toDouble() + 24) / 48
	}

	private fun sendTransposeParameters() {
		sendProperties(
				Properties(
						transpose = ((toneSlider.value * 48) - 24).roundToInt()
				)
		)
	}
}