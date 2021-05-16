package net.merayen.elastic.backend.logicnodes.list.xy_map_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

class Properties(
	var curve: FloatArray? = null, // Format: [point_x, point_y, lpoint_x, lpoint_y, rpoint_x, rpoint_y] ?
	var layoutWidth: Float? = null,
	var layoutHeight: Float? = null,
) : BaseNodeProperties()