package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.backend.nodes.BaseNodeProperties

/**
 * @param bpm Base BPM. Used if bpmCurve is null.
 * @param bpmCurve array of floats with curve data for bpm (probably going to use SignalBezierCurve to handle this data)
 * @param length How many beats that the
 */
data class Properties(
	var bufferSize: Int? = null, // TODO soon: implement support onto group_1-node
	var depth: Int? = null, // TODO soon: implement support onto group_1-node
	var sampleRate: Int? = null, // TODO soon: implement support onto group_1-node
	var bpm: Int? = null,
	var bpmCurve: FloatArray? = null,
	var length: Int? = null,

	/**
	 * Position of the playhead, in beats.
	 */
	var playheadPosition: Float? = null,

	/**
	 * Marks. Those are global, for all this node's children.
	 */
	var marks: MutableList<Mark>? = null

) : BaseNodeProperties() {

	data class Mark(
		var mark: Char? = null,
		var what: String? = null
	)

	init {
		classRegistry.add(Mark::class)
	}
}