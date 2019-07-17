package net.merayen.elastic.backend.logicnodes.list.signalgenerator_1

import net.merayen.elastic.backend.nodes.BaseNodeData

data class Data(
		val frequency: Float? = null,
		val curve: List<Float>? = null
) : BaseNodeData() {

	init {
		mapper.registerClass(Data::class)
	}
}