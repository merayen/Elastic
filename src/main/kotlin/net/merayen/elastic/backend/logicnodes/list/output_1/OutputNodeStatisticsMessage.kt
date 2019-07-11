package net.merayen.elastic.backend.logicnodes.list.output_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class OutputNodeStatisticsMessage(override val nodeId: String, val amplitudes: FloatArray, val offsets: FloatArray) : NodeDataMessage