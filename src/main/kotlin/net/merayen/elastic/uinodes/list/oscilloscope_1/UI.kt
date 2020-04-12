package net.merayen.elastic.uinodes.list.oscilloscope_1

import net.merayen.elastic.backend.logicnodes.list.oscilloscope_1.OscilloscopeSignalDataMessage
import net.merayen.elastic.backend.logicnodes.list.oscilloscope_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
import net.merayen.elastic.ui.objects.components.oscilloscope.Oscilloscope
import net.merayen.elastic.ui.objects.node.UINode
import net.merayen.elastic.ui.objects.node.UIPort

class UI : UINode() {
	private val oscilloscope = Oscilloscope()

	override fun onInit() {
		super.onInit()
		layoutWidth = 240f
		layoutHeight = 200f

		oscilloscope.handler = object : Oscilloscope.Handler {
			override fun onSettingChange() {
				sendMessage(
					NodePropertyMessage(
						nodeId,
						Properties(
							amplitude = oscilloscope.amplitude,
							offset = oscilloscope.offset,
							time = oscilloscope.time,
							trigger = oscilloscope.trigger
						)
					)
				)
			}

			override fun onAutoChange(auto: Boolean) {
				sendMessage(
					NodePropertyMessage(
						nodeId,
						Properties(
							auto = auto
						)
					)
				)
			}
		}
		oscilloscope.translation.x = 10f
		oscilloscope.translation.y = 20f
		oscilloscope.layoutWidth = 220f
		oscilloscope.layoutHeight = 160f
		add(oscilloscope)
	}

	override fun onCreatePort(port: UIPort) {
		when (port.name) {
			"in" -> port.translation.y = 20f
		}
	}

	override fun onRemovePort(port: UIPort) {}

	override fun onProperties(properties: BaseNodeProperties) {
		properties as Properties

		val amplitude = properties.amplitude
		val offset = properties.offset
		val time = properties.time
		val trigger = properties.trigger
		val auto = properties.auto

		if (amplitude != null)
			oscilloscope.amplitude = amplitude

		if (offset != null)
			oscilloscope.offset = offset

		if (time != null)
			oscilloscope.time = time

		if (trigger != null)
			oscilloscope.trigger = trigger

		if (auto != null)
			oscilloscope.auto = auto
	}

	override fun onData(message: NodeDataMessage) {
		if (message is OscilloscopeSignalDataMessage) {
			oscilloscope.samples = message.samples.toFloatArray()
		}
	}
}