package net.merayen.elastic.backend.logicnodes.list.midi_poly_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

data class Properties(
	var midiScoreData: List<Byte>? = null,
	var midiScoreDataTiming: List<Double>? = null,
) : BaseNodeProperties()