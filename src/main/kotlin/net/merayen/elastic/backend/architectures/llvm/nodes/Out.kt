package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeOutputData
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.nio.ByteBuffer

/**
 * Takes audio or signal and sends it out of the current group.
 *
 * If this node is under the topmost node, it probably gets played onto your speakers.
 *
 * TODO should we really support signal too...?
 */
class Out(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteDataSender(codeWriter: CodeWriter) {
			with(codeWriter) {
				// Clear our output buffer first
				val length = frameSize * channelCount * 4
				alloc.writeCalloc(codeWriter, "float*", "output", "1", length.toString())

				if (getInletType("in") == Format.SIGNAL) {
					writeForEachVoice(codeWriter) {
						writeForEachSample(codeWriter) {
							for (channelIndex in 0 until channelCount) {
								val offset = channelIndex * frameSize
								Statement("output[sample_index + $offset] += ${writeInlet("in")}.signal[sample_index + $offset]")
							}
						}
					}
				} else if (getInletType("in") == Format.AUDIO) {
					writeForEachVoice(codeWriter) {
						writeForEachChannel(codeWriter) {
							writeForEachSample(codeWriter) {
								Statement("output[sample_index + channel_index * $frameSize] += ${writeInlet("in")}.audio[sample_index + channel_index * $frameSize]")
							}
						}
					}
				}

				Call("send", "$length, output")

				alloc.writeFree(codeWriter, "output")
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> {
		val audio = ArrayList<FloatArray>()

		for (channel in 0 until channelCount) {
			val channelAudio = FloatArray(frameSize)
			for (sample in 0 until frameSize) {
				channelAudio[sample] = data.float
			}
			audio.add(channelAudio)
		}

		return listOf(
			Output1NodeOutputData(
				nodeId = nodeId,
				audio = Array(channelCount) { audio[it] },
				amplitudes = FloatArray(channelCount), // TODO
				offsets = FloatArray(channelCount),
				sampleRate = shared.sampleRate,
				depth = shared.depth,
				bufferSize = shared.frameSize
			)
		)
	}
}