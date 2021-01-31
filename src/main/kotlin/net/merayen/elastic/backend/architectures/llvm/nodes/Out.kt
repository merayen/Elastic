package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeOutputData
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.NodePropertyMessage
import java.nio.ByteBuffer

class Out(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteDataSender(codeWriter: CodeWriter) {
			with(codeWriter) {
				// Clear our output buffer first
				val length = frameSize * channelCount * 4
				alloc.writeCalloc(codeWriter, "float*", "output", "1", length.toString())

				writeForEachVoice(codeWriter) {
					writeForEachChannel(codeWriter) {
						writeForEachSample(codeWriter) {
							Statement("output[sample_index + channel_index * $frameSize] += ${writeInlet("in")}.signal[sample_index + channel_index * $frameSize]")
						}
					}
				}
				Call("send", "$length, output")
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> {
		val audio = ArrayList<FloatArray>()

		for (channel in 0 until channelCount) {
			val channelAudio = FloatArray(frameSize)
			for (sample in 0 until frameSize)
				channelAudio[sample] = data.float
			audio.add(channelAudio)
		}

		return listOf(Output1NodeOutputData(
			nodeId = nodeId,
			audio = Array(channelCount) { audio[it] },
			amplitudes = FloatArray(channelCount), // TODO
			offsets = FloatArray(channelCount),
			sampleRate = shared.sampleRate, // TODO
			depth = shared.depth,
			bufferSize = shared.frameSize
		))
	}
}