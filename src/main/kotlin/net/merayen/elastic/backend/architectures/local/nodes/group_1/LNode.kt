package net.merayen.elastic.backend.architectures.local.nodes.group_1

import net.merayen.elastic.backend.architectures.local.GroupLNode
import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1InputFrameData
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java), GroupLNode {
	private var currentBeatPosition = 0.0
	//private var currentSamplePosition = 0L

	private var currentCursorBeatPosition = 0.0
	private var currentCursorTimePosition = 0.0

	private var playing = false

	override fun getCurrentBarDivision(): Int {
		val parent = parent as? GroupLNode
		if (parent != null)
			return getCurrentBarDivision()

		return 4 // TODO not to be hardcoded
	}

	@Synchronized
	override fun getCurrentFrameBPM(): Double {
		val parent = parent as? GroupLNode
		if (parent != null)
			return getCurrentFrameBPM()

		return 120.0 // TODO either return cached value, or calculate from curve
	}

	override fun getCursorPosition(): Double {
		val parent = parent as? GroupLNode
		if (parent != null)
			return getCursorPosition()

		return currentCursorBeatPosition
	}

	override fun getCursorTimePosition(): Double {
		TODO()
		val parent = parent as? GroupLNode
		if (parent != null)
			return getCursorTimePosition()

		return currentCursorTimePosition
	}

	//override fun getCursorSamplePosition() = currentSamplePosition

	override fun getBeatPosition() = currentBeatPosition

	//override fun getSamplePosition() = currentSamplePosition

	override fun isPlaying() = playing

	override fun playStartedCount(): Long {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor) {}

	override fun onProcess(data: InputFrameData) {
		data as Group1InputFrameData

		val startPlaying = data.startPlaying ?: false
		val stopPlaying = data.stopPlaying ?: false
		val cursorBeatPosition = data.cursorBeatPosition

		if (startPlaying && !stopPlaying)
			playing = true

		if (stopPlaying)
			playing = false

		if (cursorBeatPosition != null)
			currentBeatPosition = cursorBeatPosition
	}

	override fun onParameter(instance: BaseNodeData) {}

	override fun onFinishFrame() {
		if (playing) {
			currentCursorBeatPosition += (buffer_size / sample_rate.toDouble()) * (getCurrentFrameBPM() / 60.0)
			currentBeatPosition = currentCursorBeatPosition % getCurrentBarDivision()
		} else {
			currentBeatPosition += (buffer_size / sample_rate.toDouble()) * (getCurrentFrameBPM() / 60.0)
			currentBeatPosition %= getCurrentBarDivision()
		}

		//currentSamplePosition += buffer_size
	}

	override fun onDestroy() {}
}
