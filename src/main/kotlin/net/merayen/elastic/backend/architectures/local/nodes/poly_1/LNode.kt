package net.merayen.elastic.backend.architectures.local.nodes.poly_1

import net.merayen.elastic.backend.architectures.local.GroupLNode
import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.poly_1.Data
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java), GroupLNode {
	override fun getCurrentFrameBPM() = (parent as GroupLNode).getCurrentFrameBPM()
	override fun getCurrentBarDivision() = (parent as GroupLNode).getCurrentBarDivision()
	//override fun getSamplePosition() = (parent as GroupLNode).getSamplePosition()
	override fun getBeatPosition() = (parent as GroupLNode).getBeatPosition()
	override fun getCursorPosition() = (parent as GroupLNode).getCursorPosition()
	override fun getCursorTimePosition() = (parent as GroupLNode).getCursorTimePosition()
	//override fun getCursorSamplePosition() = (parent as GroupLNode).getCursorSamplePosition()
	override fun isPlaying() = (parent as GroupLNode).isPlaying()
	override fun playStartedCount() = (parent as GroupLNode).playStartedCount()

	var unison = 1

	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor) {}
	override fun onProcess(data: InputFrameData) {}

	override fun onParameter(instance: BaseNodeData) {
		val data = instance as Data
		val unisonData = data.unison

		if (unisonData != null)
			unison = unisonData
	}

	override fun onFinishFrame() {}

	override fun onDestroy() {}
}
