package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.Temporary
import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeProperties
import net.merayen.elastic.backend.nodes.GroupLogicNode
import net.merayen.elastic.system.intercom.InputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData

/**
 * Doesn't do anything, other than having children.
 */
class LogicNode : BaseLogicNode(), GroupLogicNode {
	private var startPlaying = false
	private var stopPlaying = false
	private var playheadPosition: Float? = null
	private var rangeSelectionStart: Float? = null
	private var rangeSelectionStop: Float? = null
	private var bpm = 120.0
	private var isPlaying = false

	private var sampleRate: Int = Temporary.sampleRate
	private var bufferSize: Int = Temporary.bufferSize
	private var depth: Int = Temporary.depth

	private var nextReportToUI = System.currentTimeMillis()

	override fun onInit() {}
	override fun onConnect(port: String) {}
	override fun onDisconnect(port: String) {}
	override fun onRemove() {}

	override fun onData(message: NodeDataMessage) {
		when (message) {
			is SetBPMMessage -> {
				bpm = message.bpm.toDouble()
				updateProperties(Properties(bpm = bpm.toInt()))
			}
			is TransportStartPlaybackMessage -> {
				startPlaying = true
				isPlaying = true
			}
			is TransportStopPlaybackMessage -> {
				stopPlaying = true
				isPlaying = false
			}
		}
	}

	override fun onParameterChange(instance: BaseNodeProperties) {
		instance as Properties

		val bpm = instance.bpm
		val playheadPosition = instance.playheadPosition
		val rangeSelectionStart = instance.rangeSelectionStart
		val rangeSelectionStop = instance.rangeSelectionStop

		if (bpm != null)
			this.bpm = bpm.toDouble()

		if (playheadPosition != null)
			this.playheadPosition = playheadPosition

		if (rangeSelectionStart != null && rangeSelectionStop != null) {
			this.rangeSelectionStart = rangeSelectionStart
			this.rangeSelectionStop = rangeSelectionStop
		}

		updateProperties(instance)
	}

	override fun onPrepareFrame(): InputFrameData {
		val data = Group1InputFrameData(
			id,
			startPlaying = startPlaying,
			stopPlaying = stopPlaying,
			playheadPosition = playheadPosition,
			bpm = bpm, // TODO remove this and use a curve instead
			sampleRate = sampleRate,
			bufferSize = bufferSize,
			depth = depth
		)

		startPlaying = false
		stopPlaying = false
		playheadPosition = null

		return data
	}

	override fun onFinishFrame(data: OutputFrameData?) {
		data as? Group1OutputFrameData ?: return

		if (nextReportToUI < System.currentTimeMillis()) {
			nextReportToUI = System.currentTimeMillis() + 50
			sendMessage(PlaybackStatusMessage(nodeId = id, currentPlayheadPosition = data.currentPlayheadPosition, currentBPM = data.currentBPM, isPlaying = isPlaying))
		}
	}
}
