package net.merayen.elastic.backend.logicnodes.list.meter_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

class Properties(
	var minValue: Float? = null,
	var maxValue: Float? = null,
	var auto: Boolean? = null,
) : BaseNodeProperties()