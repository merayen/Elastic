package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeAudioOut
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeSignalOut
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.nio.ByteBuffer

/**
 * Takes audio or signal and sends it out of the current group.
 *
 * The parent node decides what happens with the data send into this node. If this node is under the topmost node,
 * it probably gets played onto your speakers.
 */
class Out(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteDataSender(codeWriter: CodeWriter) {
			with(codeWriter) {
				// Clear our output buffer first

				if (getInletType("in") == Format.SIGNAL) {
					alloc.writeCalloc(codeWriter, "float*", "output", "1", frameSize.toString())
					writeForEachVoice(codeWriter) {
						writeForEachSample(codeWriter) {
							Statement("output[sample_index] += ${writeInlet("in")}.signal[sample_index]")
						}
					}

				} else if (getInletType("in") == Format.AUDIO) {
					val length = frameSize * channelCount * 4
					alloc.writeCalloc(codeWriter, "float*", "output", "1", length.toString())
					writeForEachVoice(codeWriter) {
						writeForEachChannel(codeWriter) {
							writeForEachSample(codeWriter) {
								Statement("output[sample_index + channel_index * $frameSize] += ${writeInlet("in")}.audio[sample_index + channel_index * $frameSize]")
							}
						}
					}
					Call("send", "$length, output")

				} else if (getInletType("in") == Format.MIDI) {
					Member("int", "count")
					writeForEachVoice(codeWriter) {
						Statement("count += ${writeInlet("in")}.count")
					}
					alloc.writeMalloc(codeWriter, "short*", "output", "count * 4")

					Member("int", "offset = 0")
					writeForEachVoice(codeWriter) {
						Call("memcpy", "output + offset, ${writeInlet("in")}.midi, ${writeInlet("in")}.count * 4")
						Statement("offset += ${writeInlet("in")}.count * 4")
					}

					alloc.writeFree(codeWriter, "output")
				}

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

		if (getInletType("in") == Format.AUDIO) {
			return listOf(
				Output1NodeAudioOut(
					nodeId = nodeId,
					audio = Array(channelCount) { audio[it] },
					amplitudes = FloatArray(channelCount), // TODO
					offsets = FloatArray(channelCount),
					sampleRate = shared.sampleRate,
					depth = shared.depth,
					bufferSize = shared.frameSize
				)
			)
		} else if (getInletType("in") == Format.SIGNAL) {
			return listOf(
				Output1NodeSignalOut()
			)
		}
	}
}