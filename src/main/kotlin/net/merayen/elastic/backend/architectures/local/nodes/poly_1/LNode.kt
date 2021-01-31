package net.merayen.elastic.backend.architectures.local.nodes.poly_1

import net.merayen.elastic.backend.architectures.local.GroupLNode
import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.poly_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java), GroupLNode {
	override fun getSampleRate() = (parent as GroupLNode).getSampleRate()
	override fun getBufferSize() = (parent as GroupLNode).getBufferSize()
	override fun getDepth() = (parent as GroupLNode).getDepth()
	override fun getChannelCount() = 1 // Poly only deals with mono for now. Should support more channels later

	override fun getCurrentFrameBPM() = (parent as GroupLNode).getCurrentFrameBPM()
	override fun getCurrentBarDivision() = (parent as GroupLNode).getCurrentBarDivision()
	override fun getBeatPosition() = (parent as GroupLNode).getBeatPosition()
	override fun getCursorPosition() = (parent as GroupLNode).getCursorPosition()
	override fun getCursorTimePosition() = (parent as GroupLNode).getCursorTimePosition()
	override fun isPlaying() = (parent as GroupLNode).isPlaying()
	override fun playStartedCount() = (parent as GroupLNode).playStartedCount()
	override fun getRangeSelection() = (parent as GroupLNode).getRangeSelection()

	var unison = 1

	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor) {}
	override fun onProcess(data: InputFrameData) {}

	override fun onParameter(instance: BaseNodeProperties) {
		val data = instance as Properties
		val unisonData = data.unison

		if (unisonData != null)
			unison = unisonData
	}

	override fun onFinishFrame() {}

	override fun onDestroy() {}
}
