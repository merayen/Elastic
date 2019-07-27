package net.merayen.elastic.backend.logicnodes.list.wave_1

import net.merayen.elastic.backend.nodes.BaseNodeData

data class Data(var type: String? = null) : BaseNodeData() {
	enum class Type {
		SINE,
		TRIANGLE,
		SQUARE,
		NOISE
	}
}