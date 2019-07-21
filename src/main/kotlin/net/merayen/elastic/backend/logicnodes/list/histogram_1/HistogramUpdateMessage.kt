package net.merayen.elastic.backend.logicnodes.list.histogram_1

import net.merayen.elastic.system.intercom.NodeDataMessage

class HistogramUpdateMessage(override val nodeId: String, var buckets: Array<Float>? = null) : NodeDataMessage