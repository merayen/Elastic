package net.merayen.elastic.backend.architectures.local.nodes.histogram_1

import net.merayen.elastic.system.intercom.OutputFrameData

class Histogram1NodeOutputFrameData(nodeId: String, var buckets: Array<Float>? = null) : OutputFrameData(nodeId)