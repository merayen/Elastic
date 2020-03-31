package net.merayen.elastic.backend.architectures.local.nodes.arithmetic_1

import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.arithmetic_1.Mode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java) {
	var mode = Mode.ADDITION
		private set

	override fun onProcess(data: InputFrameData?) {}
	override fun onParameter(instance: BaseNodeProperties?) {}
	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor?) {}
	override fun onFinishFrame() {}
	override fun onDestroy() {}
}