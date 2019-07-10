package net.merayen.elastic.backend.logicnodes.list.output_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class OutputNodeStatisticsData(
		override val nodeId: String,
		val device: String,
		val availableBeforeAvg: Double,
		val availableBeforeMin: Double,
		val availableAfterAvg: Double,
		val availableAfterMin: Double
) : NodeDataMessage