package net.merayen.elastic.backend.logicnodes.list.output_1

import net.merayen.elastic.system.intercom.OutputFrameData

/**
 * Message from the processing backend.
 *
 * TODO should also contain MIDI output data
 */
class Output1NodeAudioOut(
	override val nodeId: String,
	val audio: Array<FloatArray?>,
	val amplitudes: FloatArray,
	val offsets: FloatArray,
	val sampleRate: Int,
	val depth: Int,
	val bufferSize: Int
) : OutputFrameData(nodeId)