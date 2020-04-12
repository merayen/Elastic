package net.merayen.elastic.backend.logicnodes.list.oscilloscope_1

import net.merayen.elastic.system.intercom.OutputFrameData

class OscilloscopeSignalDataMessage(
	nodeId: String,
	val samples: Array<Float>,
	val minValue: Float,
	val maxValue: Float
) : OutputFrameData(nodeId)
