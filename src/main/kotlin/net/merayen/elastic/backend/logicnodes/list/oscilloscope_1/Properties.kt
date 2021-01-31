package net.merayen.elastic.backend.logicnodes.list.oscilloscope_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

data class Properties(
	var amplitude: Float? = null,
	var offset: Float? = null,
	var time: Float? = null,
	var trigger: Float? = null,
	var auto: Boolean? = null
) : BaseNodeProperties()
