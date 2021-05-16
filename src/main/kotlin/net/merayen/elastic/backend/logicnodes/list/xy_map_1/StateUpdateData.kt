package net.merayen.elastic.backend.logicnodes.list.xy_map_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class StateUpdateData(
	override val nodeId: String,
	val positions: FloatArray, // 0f to 1f where the current X-position is
	) : NodeDataMessage