package net.merayen.elastic.backend.logicnodes.list.signalgenerator_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

data class Properties(
		var inputAmplitude: Float? = null,
		var inputOffset: Float? = null,
		var frequency: Float? = null,
		var curve: List<Float>? = null
) : BaseNodeProperties() {
	init {
		listTranslators["curve"] = {
			(it as Number).toFloat()
		}
	}
}