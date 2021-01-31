package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class PlaybackStatusMessage(
		override val nodeId: String,
		val currentPlayheadPosition: Float,
		val currentBPM: Float,
		val isPlaying: Boolean
) : NodeDataMessage
