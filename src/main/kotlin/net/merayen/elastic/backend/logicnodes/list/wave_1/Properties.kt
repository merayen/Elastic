package net.merayen.elastic.backend.logicnodes.list.wave_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

data class Properties(var type: String? = null, val frequency: Float? = null) : BaseNodeProperties() {
	enum class Type {
		SINE,
		TRIANGLE,
		SQUARE,
		NOISE,
		SAW
	}
}