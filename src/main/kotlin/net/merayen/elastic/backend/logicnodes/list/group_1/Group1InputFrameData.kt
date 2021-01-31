package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.system.intercom.InputFrameData

class Group1InputFrameData(
		nodeId: String,
		val startPlaying: Boolean? = null,
		val stopPlaying: Boolean? = null,
		val playheadPosition: Float? = null,
		val bpm: Double? = null,
		val sampleRate: Int? = null,
		val bufferSize: Int? = null,
		val depth: Int? = null
) : InputFrameData(nodeId)