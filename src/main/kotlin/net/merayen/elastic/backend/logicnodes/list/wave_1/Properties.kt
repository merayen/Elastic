package net.merayen.elastic.backend.logicnodes.list.wave_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

data class Properties(
	var type: String? = null,
	var frequency: Float? = null,
	var inputAmplitude: Float? = null,
	var inputOffset: Float? = null,
	var curve: List<Float>? = null
) : BaseNodeProperties() {
	enum class Type {
		SINE,
		TRIANGLE,
		SQUARE,
		NOISE,
		SAW,
		CURVE,
	}

	init {
		listTranslators["curve"] = {
			(it as Number).toFloat()
		}
	}
}