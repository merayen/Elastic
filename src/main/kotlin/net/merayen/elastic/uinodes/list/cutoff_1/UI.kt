package net.merayen.elastic.uinodes.list.cutoff_1

import net.merayen.elastic.backend.logicnodes.list.cutoff_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.ui.UIObject
import net.merayen.elastic.ui.objects.components.Knob
import net.merayen.elastic.ui.objects.components.framework.PortParameter
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort
import kotlin.math.pow
import kotlin.math.roundToInt

class UI : UINode() {
	private val frequencyKnob = Knob()
	private val dampingKnob = Knob()

	init {
		layoutWidth = 120f
		layoutHeight = 80f
		titlebar.title = "Cutoff"

		frequencyKnob.label.text = "Frequency"
		frequencyKnob.translation.x = 20f
		frequencyKnob.translation.y = 30f
		frequencyKnob.handler = object : Knob.Handler {
			override fun onChange(value: Float) = sendProperties(Properties(frequency = 1 + value.pow(2) * 10))
			override fun onLabelUpdate(value: Float) = (1 + value.pow(2) * 20).roundToInt().toString()
		}

		dampingKnob.label.text = "Damping"
		dampingKnob.translation.x = 70f
		dampingKnob.translation.y = 30f
		dampingKnob.handler = object : Knob.Handler {
			override fun onChange(value: Float) = sendProperties(Properties(damping = 1 + value.pow(2) * 10))
			override fun onLabelUpdate(value: Float) = (1 + value.pow(2) * 10).toInt().toString()
		}
	}

	override fun onCreatePort(port: UIPort) {
		when (port.name) {
			"in" -> {
				port.translation.y = 20f
			}
			"out" -> {
				port.translation.x = layoutWidth
				port.translation.y = 20f
			}
			"frequency" -> {
				add(PortParameter(this, port, frequencyKnob, UIObject()))
				port.translation.y = 40f
			}
			"damping" -> {
				add(PortParameter(this, port, dampingKnob, UIObject()))
				port.translation.y = 60f
			}
		}
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onProperties(properties: BaseNodeProperties) {
		properties as Properties

		val frequency = properties.frequency
		val damping = properties.damping

		if (frequency != null)
			frequencyKnob.value = ((frequency - 1) / 10f).pow(0.5f)

		if (damping != null)
			dampingKnob.value = ((damping - 1) / 10f).pow(0.5f)
	}

	override fun onData(message: NodeDataMessage) {}
}