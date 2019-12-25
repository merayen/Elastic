package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.system.intercom.NodeMessage

class PlaybackStatusMessage(
	override val nodeId: String,
	val currentPlayheadPosition: Float
	) : NodeMessage
