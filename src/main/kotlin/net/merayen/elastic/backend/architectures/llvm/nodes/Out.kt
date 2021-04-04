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
 *
 * TODO We might want the parent node of this Out-node to actually read the data it receives, in the C code...? Not just forward it? The parent node probably wants to process the data... Maybe store the output data in a buffer instead? Or just let the parent node read the outlet connected to this node?
 */
class Out(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex) {
	override val nodeClass = object : NodeClass() {
		override fun onWriteDataSender(codeWriter: CodeWriter) {
			with(codeWriter) {
				// Clear our output buffer first
				when (getInletType("in")) {
					null -> return
					Format.SIGNAL -> {
						alloc.writeCalloc(codeWriter, "float*", "output", "1", "$frameSize * 4")
						writeForEachVoice(codeWriter) {
							writeForEachSample(codeWriter) {
								Statement("output[sample_index] += ${writeInlet("in")}.signal[sample_index]")
							}
						}

						Call("send", "$frameSize * sizeof(float), output")

						alloc.writeFree(codeWriter, "output")
					}

					Format.AUDIO -> {
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

					Format.MIDI -> {
						Member("int", "length = 0")
						writeForEachVoice(codeWriter) {
							Statement("length += ${writeInlet("in")}.length")
						}

						If("length > 0") { // Only send midi output if there is any
							alloc.writeMalloc(codeWriter, "short*", "output", "length * sizeof(char) * 3")

							Member("int", "offset = 0")
							writeForEachVoice(codeWriter) {
								Call(
									"memcpy",
									"output + offset, ${writeInlet("in")}.messages, ${writeInlet("in")}.length * sizeof(char) * 3"
								)
								Statement("offset += ${writeInlet("in")}.length * sizeof(char) * 3")
							}
							Call("send", "length, output")

							alloc.writeFree(codeWriter, "output")
						}
						Else {
							Call("send", "0, NULL") // No MIDI to send, we send nothing
						}
					}
					else -> TODO("port format '${getInletType("in")}' not supported yet")
				}
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> { // TODO out node should send nothing! Remove whole function!
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
				for (i in 0 until frameSize)
					signal[i] = data.float

				listOf(
					Output1NodeSignalOut(nodeId, signal = signal)
				)
			}

			Format.MIDI -> {
				val midi = ShortArray(data.limit())
				for (i in 0 until data.limit()) {
					val v = data.get().toShort()
					if (v >= 0)
						midi[i] = v
					else
						midi[i] = (v + 256).toShort()
				}

				listOf(
					Output1NodeMidiOut(nodeId, midi = midi)
				)
			}
			else -> listOf<OutputFrameData>()
		}
	}
}