package net.merayen.elastic.backend.architectures.local.nodes.metronome_1

import net.merayen.elastic.backend.architectures.local.GroupLNode
import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.metronome_1.Metronome1OutputFrameData
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java) {
	private var lastBeat = 0

	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor?) {}
	override fun onProcess(data: InputFrameData?) {}
	override fun onParameter(instance: BaseNodeProperties?) {}
	override fun onFinishFrame() {
		val parent = parent as GroupLNode
		val currentBeatPosition = parent.getBeatPosition().toInt()
		if (lastBeat != currentBeatPosition) {
			outgoing = Metronome1OutputFrameData(id, currentBeat = currentBeatPosition, currentDivision = parent.getCurrentBarDivision())
			lastBeat = currentBeatPosition
		}
	}

	override fun onDestroy() {}
}