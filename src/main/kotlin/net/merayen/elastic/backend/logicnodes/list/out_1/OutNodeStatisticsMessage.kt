package net.merayen.elastic.backend.logicnodes.list.out_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class OutNodeStatisticsMessage(
	override val nodeId: String,
	val amplitudes: FloatArray,
	val offsets: FloatArray
) : NodeDataMessage