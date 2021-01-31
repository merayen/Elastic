package net.merayen.elastic.backend.architectures.local.nodes.group_1

import net.merayen.elastic.Temporary
import net.merayen.elastic.backend.architectures.local.GroupLNode
import net.merayen.elastic.backend.architectures.local.LocalNode
import net.merayen.elastic.backend.architectures.local.LocalProcessor
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1InputFrameData
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.backend.logicnodes.list.group_1.Properties
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.system.intercom.InputFrameData

class LNode : LocalNode(LProcessor::class.java), GroupLNode {
	private var sampleRate: Int? = null
	private var depth: Int? = null
	private var bufferSize: Int? = null

	private var currentBeatPosition = 0.0

	private var currentCursorBeatPosition = 0.0
	private var lastCursorBeatPosition = 0.0
	private var currentCursorTimePosition = 0.0
	private var channelCount = 1

	private var rangeSelection = Pair(0f, 0f)

	private var playCount = 0L

	private var bpm = 120.0

	private var playing = false

	override fun getBufferSize(): Int {
		val parent = parent as? GroupLNode
		if (parent != null)
			return parent.getBufferSize()

		return Temporary.bufferSize // FIXME should be received from our LogicNode
	}

	override fun getSampleRate() = Temporary.sampleRate
	override fun getDepth() = Temporary.depth
	override fun getChannelCount() = channelCount

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

		return bpm
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

	override fun getBeatPosition() = currentBeatPosition

	override fun isPlaying() = playing

	override fun playStartedCount(): Long {
		val parent = parent as? GroupLNode
		if (parent != null)
			playCount = parent.playStartedCount()

		return playCount
	}

	override fun getRangeSelection() = rangeSelection

	override fun onInit() {}
	override fun onSpawnProcessor(lp: LocalProcessor) {}

	override fun onProcess(data: InputFrameData) {
		data as Group1InputFrameData

		val startPlaying = data.startPlaying ?: false
		val stopPlaying = data.stopPlaying ?: false
		val playheadPosition = data.playheadPosition
		val bpm = data.bpm

		if (startPlaying && !stopPlaying)
			playing = true

		if (stopPlaying)
			playing = false

		val rangeSelection = rangeSelection
		if (playheadPosition != null) {
			this.currentCursorBeatPosition = playheadPosition.toDouble()
			playCount++
		} else if (rangeSelection.first != rangeSelection.second && isPlaying()) { // If there is a range selection set, see if we should move the playhead to the beginning if it has reached the end
			if (currentCursorBeatPosition >= rangeSelection.second)
				currentCursorBeatPosition = rangeSelection.first.toDouble()
		}

		lastCursorBeatPosition = currentCursorBeatPosition

		if (bpm != null)
			this.bpm = bpm
	}

	override fun onParameter(instance: BaseNodeProperties) {
		instance as Properties

		val channelCount = instance.channelCount
		if (channelCount != null)
			this.channelCount = channelCount

		val rangeSelectionStart = instance.rangeSelectionStart
		val rangeSelectionStop = instance.rangeSelectionStop

		if (rangeSelectionStart != null && rangeSelectionStop != null)
			this.rangeSelection = Pair(rangeSelectionStart, rangeSelectionStop)
	}

	override fun onFinishFrame() {
		if (playing) {
			currentCursorBeatPosition += (buffer_size / sample_rate.toDouble()) * (getCurrentFrameBPM() / 60.0)
			currentBeatPosition = currentCursorBeatPosition % getCurrentBarDivision()
		} else {
			currentBeatPosition += (buffer_size / sample_rate.toDouble()) * (getCurrentFrameBPM() / 60.0)
			currentBeatPosition %= getCurrentBarDivision()
		}

		// Report back playback position
		outgoing = Group1OutputFrameData(nodeId = id, currentPlayheadPosition = currentCursorBeatPosition.toFloat(), currentBPM = bpm.toFloat())
	}

	override fun onDestroy() {}
}
