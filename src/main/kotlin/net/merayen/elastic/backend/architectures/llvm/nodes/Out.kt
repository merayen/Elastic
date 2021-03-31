package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeAudioOut
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeMidiOut
import net.merayen.elastic.backend.logicnodes.list.output_1.Output1NodeSignalOut
import net.merayen.elastic.system.intercom.NodeDataMessage
import net.merayen.elastic.system.intercom.OutputFrameData
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

				when {
					getInletType("in") == Format.SIGNAL -> {
						alloc.writeCalloc(codeWriter, "float*", "output", "1", frameSize.toString())
						writeForEachVoice(codeWriter) {
							writeForEachSample(codeWriter) {
								Statement("output[sample_index] += ${writeInlet("in")}.signal[sample_index]")
							}
						}

						Call("send", "$frameSize, output")

						alloc.writeFree(codeWriter, "output")

					}
					getInletType("in") == Format.AUDIO -> {
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

						alloc.writeFree(codeWriter, "output")

					}
					getInletType("in") == Format.MIDI -> {
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
				}
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> {
		return when (getInletType("in")) {
			Format.AUDIO -> {
				val audio = ArrayList<FloatArray>()

				for (channel in 0 until channelCount) {
					val channelAudio = FloatArray(frameSize)
					for (sample in 0 until frameSize) {
						channelAudio[sample] = data.float
					}
					audio.add(channelAudio)
				}

				listOf(
					Output1NodeAudioOut(
						nodeId = nodeId,
						audio = Array(channelCount) { audio[it] },
						amplitudes = FloatArray(channelCount), // TODO
						offsets = FloatArray(channelCount),
						sampleRate = shared.sampleRate,
						depth = shared.depth,
						bufferSize = frameSize
					)
				)
			}

			Format.SIGNAL -> {
				val signal = FloatArray(frameSize)
				for (sample in 0 until frameSize)
					signal[sample] = data.float

				listOf(
					Output1NodeSignalOut(nodeId, signal = signal)
				)
			}

			Format.MIDI -> {
				val midi = ShortArray(data.limit())
				for (i in 0 until data.limit())
					midi[i] = data.get().toShort()

				listOf(
					Output1NodeMidiOut(nodeId, midi = midi)
				)
			}
			else -> listOf<OutputFrameData>()
		}
	}
}