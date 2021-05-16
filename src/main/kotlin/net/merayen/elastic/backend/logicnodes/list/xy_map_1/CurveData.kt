package net.merayen.elastic.backend.logicnodes.list.xy_map_1

import net.merayen.elastic.system.intercom.NodeDataMessage

/**
 * @param curve as Y-axis data points. Allowed values are 0f including 1f
 */
class CurveData(
	override val nodeId: String,
	val curve: FloatArray,
) : NodeDataMessage