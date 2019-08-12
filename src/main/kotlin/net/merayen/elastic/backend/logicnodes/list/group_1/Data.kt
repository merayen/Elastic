package net.merayen.elastic.backend.logicnodes.list.group_1

import net.merayen.elastic.backend.nodes.BaseNodeData

/**
 * @param bpm Base BPM. Used if bpmCurve is null.
 * @param bpmCurve array of floats with curve data for bpm (probably going to use SignalBezierCurve to handle this data)
 * @param length How many beats that the
 */
data class Data(
	var bpm: Int? = null,
	var bpmCurve: FloatArray? = null,
	var length: Int? = null
) : BaseNodeData()