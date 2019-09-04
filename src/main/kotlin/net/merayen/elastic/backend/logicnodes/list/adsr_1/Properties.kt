package net.merayen.elastic.backend.logicnodes.list.adsr_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

class Properties(
		var attack: Float? = null,
		var decay: Float? = null,
		var sustain: Float? = null,
		var release: Float? = null
) : BaseNodeProperties()