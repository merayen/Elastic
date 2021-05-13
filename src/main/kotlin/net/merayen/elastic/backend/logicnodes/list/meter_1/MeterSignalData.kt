package net.merayen.elastic.backend.logicnodes.list.meter_1

import net.merayen.elastic.system.intercom.OutputFrameData

class MeterSignalData(
	nodeId: String,
	val value: Float,
) : OutputFrameData(nodeId)