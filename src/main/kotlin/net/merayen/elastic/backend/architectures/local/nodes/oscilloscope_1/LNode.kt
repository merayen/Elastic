package net.merayen.elastic.backend.architectures.local.nodes.oscilloscope_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.oscilloscope_1.OscilloscopeSignalDataMessage
import net.merayen.elastic.backend.logicnodes.list.oscilloscope_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData
import kotlin.math.max
import kotlin.math.min

class LNode : LocalNode(LProcessor::class.java) {
	var amplitude = 1f
	var offset = 0f
	var time = 0.001f
	var trigger = 0f

	override fun onParameter(instance: BaseNodeProperties?) {
		instance as Properties
		amplitude = max(0f, min(1000f, instance.amplitude ?: amplitude))
		offset = max(-1000f, min(1000f, instance.offset ?: offset))
		time = max(0.000001f, min(1f, instance.time ?: time))
		trigger = max(-1000f, min(1000f, instance.trigger ?: trigger))
	}

	override fun onProcess(data: InputFrameData?) {}
	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor?) {}

	@ExperimentalStdlibApi
	override fun onFinishFrame() {
		val processor = processors.filter { (it as LProcessor).doneSampling }.randomOrNull() as? LProcessor ?: return

		outgoing = OscilloscopeSignalDataMessage(
			nodeId = id,
			samples = processor.samples.toTypedArray()
		)
	}

	override fun onDestroy() {}
}