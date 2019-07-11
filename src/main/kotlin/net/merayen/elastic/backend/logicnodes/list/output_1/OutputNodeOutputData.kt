package net.merayen.elastic.backend.logicnodes.list.output_1

import net.merayen.elastic.system.intercom.OutputFrameData

/**
 * Message from the processing backend.
 */
class OutputNodeOutputData(
		override val nodeId: String,
		val audio: Array<FloatArray?>,
		val amplitudes: FloatArray,
		val offsets: FloatArray
) : OutputFrameData(nodeId)