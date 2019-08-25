package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.system.intercom.InputFrameData

class Group1InputFrameData(
	nodeId: String,
	val startPlaying: Boolean? = null,
	val stopPlaying: Boolean? = null,
	val cursorBeatPosition: Double? = null,
	val bpm: Double? = null
) : InputFrameData(nodeId)