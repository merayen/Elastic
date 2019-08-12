package net.merayen.elastic.backend.architectures.local.nodes.delay_1

import net.merayen.elastic.backend.architectures.local.GroupLNode
import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.delay_1.Data
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java) {
	var delaySamples: Long = 0
	var currentBpm = 0.0

	override fun onInit() {}

	override fun onSpawnProcessor(lp: LocalProcessor) {}

	override fun onProcess(data: InputFrameData) {
		currentBpm = (parent as GroupLNode).getCurrentFrameBPM()
	}

	override fun onParameter(instance: BaseNodeData) {
		val data = instance as Data
		val delayTimeData = data.delayTime

		if (delayTimeData != null)
			delaySamples = (delayTimeData.toDouble() * sample_rate).toLong()
	}

	override fun onFinishFrame() {}

	override fun onDestroy() {}
}
