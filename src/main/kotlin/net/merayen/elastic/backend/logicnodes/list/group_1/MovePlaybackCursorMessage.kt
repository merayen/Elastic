package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class MovePlaybackCursorMessage(override val nodeId: String, val beatPosition: Double) : NodeDataMessage