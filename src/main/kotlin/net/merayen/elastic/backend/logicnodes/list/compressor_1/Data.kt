package net.merayen.elastic.backend.logicnodes.list.compressor_1

import net.merayen.elastic.backend.nodes.BaseNodeData

data class Data(
		var inputAmplitude: Float? = null,
		var inputSidechainAmplitude: Float? = null,
		var outputAmplitude: Float? = null,
		var attack: Float? = null,
		var release: Float? = null,
		var ratio: Float? = null,
		var knee: Float? = null,
		var threshold: Float? = null
) : BaseNodeData()