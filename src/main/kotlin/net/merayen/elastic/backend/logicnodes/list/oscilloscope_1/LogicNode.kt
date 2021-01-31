package net.merayen.elastic.backend.logicnodes.list.oscilloscope_1

import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData
import kotlin.math.max
import kotlin.math.roundToInt

class LogicNode : BaseLogicNode() {
	private var amplitude = 0f
	private var offset = 0f
	private var trigger = 0f
	private var auto = false

	private var nextAutoParameter = 0L
	private var maxValue = 0f
	private var minValue = 0f

	override fun onInit() {
		createInputPort("in")
	}

	override fun onData(data: NodeDataMessage?) {}
	override fun onDisconnect(port: String?) {}
	override fun onConnect(port: String?) {}
	override fun onRemove() {}
	override fun onParameterChange(instance: BaseNodeProperties?) {
		instance as Properties

		amplitude = instance.amplitude ?: amplitude
		offset = instance.offset ?: offset
		trigger = instance.trigger ?: trigger
		auto = instance.auto ?: auto

		updateProperties(instance)
	}

	override fun onFinishFrame(data: OutputFrameData?) {
		if (data is OscilloscopeSignalDataMessage) {
			sendDataToUI(data)

			// In auto-mode. We change the parameters automatically by the input signal
			if (auto && nextAutoParameter < System.currentTimeMillis()) {
				nextAutoParameter = System.currentTimeMillis() + 100 // Maximum every 100ms we update the parameters

				// Update our local stats
				maxValue += (data.maxValue - maxValue) / 2
				minValue += (data.minValue - minValue) / 2

				amplitude = 1 / max(0.1f, maxValue - minValue)
				offset = (-(maxValue - (maxValue - minValue) / 2))
				trigger = -offset + (maxValue - minValue) / 2

				updateProperties(
					Properties(
						amplitude = amplitude,
						offset = offset,
						trigger = trigger
					)
				)
			}
		}
	}
}