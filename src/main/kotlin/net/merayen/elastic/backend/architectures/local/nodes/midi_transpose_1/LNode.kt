package net.merayen.elastic.backend.architectures.local.nodes.midi_transpose_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.midi_transpose_1.Data
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java) {
	var transpose = 0

	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor?) {}
	override fun onProcess(data: InputFrameData) {}

	override fun onParameter(instance: BaseNodeData) {
		val data = (instance as Data)
		val transposeData = data.transpose
		if (transposeData != null)
			transpose = transposeData
	}

	override fun onFinishFrame() {}
	override fun onDestroy() {}
}