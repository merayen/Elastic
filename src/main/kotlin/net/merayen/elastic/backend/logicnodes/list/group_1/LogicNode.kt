package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.backend.nodes.BaseLogicNode
import net.merayen.elastic.backend.nodes.BaseNodeData
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
	private var cursorBeatPosition: Double? = null
	private var bpm = 120.0

	override fun onCreate() {}
	override fun onInit() {}
	override fun onConnect(port: String) {}
	override fun onDisconnect(port: String) {}
	override fun onRemove() {}
	override fun onFinishFrame(data: OutputFrameData?) {}
	override fun onData(message: NodeDataMessage) {
		when (message) {
			is SetBPMMessage -> {
				bpm = message.bpm.toDouble()
				updateProperties(Data(bpm = bpm.toInt()))
			}
			is TransportStartPlaybackMessage -> startPlaying = true
			is TransportStopPlaybackMessage -> stopPlaying = true
			is MovePlaybackCursorMessage -> cursorBeatPosition = message.beatPosition
		}
	}

	override fun onParameterChange(instance: BaseNodeData) {
		instance as Data
		val bpm = instance.bpm
		if (bpm != null)
			this.bpm = bpm.toDouble()

		updateProperties(instance)
	}

	override fun onPrepareFrame(): InputFrameData {
		val data = Group1InputFrameData(
			id,
			startPlaying = startPlaying,
			stopPlaying = stopPlaying,
			cursorBeatPosition = cursorBeatPosition,
			bpm = bpm // TODO remove this and use a curve instead
		)

		startPlaying = false
		stopPlaying = false

		return data
	}
}
