package net.merayen.elastic.backend.logicnodes.list.adsr_1

import net.merayen.elastic.backend.nodes.BaseNodeData

class Data(
		var attack: Float? = null,
		var decay: Float? = null,
		var sustain: Float? = null,
		var release: Float? = null
) : BaseNodeData()