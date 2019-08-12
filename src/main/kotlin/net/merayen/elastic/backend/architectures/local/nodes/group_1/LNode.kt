package net.merayen.elastic.backend.architectures.local.nodes.group_1

import net.merayen.elastic.backend.architectures.local.GroupLNode
import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.nodes.BaseNodeData
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java), GroupLNode {
	/**
	 * A buffer for BPM for every sample. Children nodes retrieves this via getBPMCurveFrameBuffer and uses it for
	 * every sample to adjust the speed.
	 */
	@Volatile
	private var currentFrameBPM: Float? = null

	private var currentBeatPosition = 0.0
	private var currentSamplePosition = 0L
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

	override fun getCursorBeatPosition(): Double {
		val parent = parent as? GroupLNode
		if (parent != null)
			return getCursorBeatPosition()

		return 120.0 // TODO either return cached value, or calculate from curve
	}

	override fun getCursorTimePosition(): Double {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getCursorSamplePosition() = currentSamplePosition

	override fun getCurrentBeatPosition() = currentBeatPosition

	override fun getSamplePosition() = currentSamplePosition

	override fun isPlaying() = playing

	override fun playStartedCount(): Long {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor) {}
	override fun onProcess(data: InputFrameData) {}
	override fun onParameter(instance: BaseNodeData) {}

	override fun onFinishFrame() {
		currentBeatPosition += (buffer_size / sample_rate.toDouble()) * (getCurrentFrameBPM() / 60.0)
		currentBeatPosition %= getCurrentBarDivision()

		currentSamplePosition += buffer_size

		currentFrameBPM = null
	}

	override fun onDestroy() {}
}
