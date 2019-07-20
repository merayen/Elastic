package net.merayen.elastic.backend.logicnodes.list.signalgenerator_1

import net.merayen.elastic.backend.nodes.BaseNodeData

data class Data(
		var inputAmplitude: Float? = null,
		var inputOffset: Float? = null,
		var frequency: Float? = null,
		var curve: List<Float>? = null
) : BaseNodeData()