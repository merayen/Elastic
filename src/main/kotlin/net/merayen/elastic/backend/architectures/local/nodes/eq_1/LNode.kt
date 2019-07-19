package net.merayen.elastic.backend.architectures.local.nodes.eq_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java) {
	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor?) {}
	override fun onProcess(data: InputFrameData) {}
	override fun onParameter(instance: BaseNodeData) {}
	override fun onFinishFrame() {}
	override fun onDestroy() {}
}