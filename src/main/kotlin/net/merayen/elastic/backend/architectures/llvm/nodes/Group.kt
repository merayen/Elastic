package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.getName
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.group_1.Group1OutputFrameData
import net.merayen.elastic.system.intercom.NodeDataMessage
import java.nio.ByteBuffer

/**
 * Group node.
 *
 * I think this node can be looked at as a "project".  It is always the top-most node.
 *
 * Sends data from its direct out-node children.
 */
class Group(nodeId: String) : TranspilerNode(nodeId), GroupInterface {
	override val nodeClass = object : NodeClass() {
		override fun onWriteDataSender(codeWriter: CodeWriter) { // Send data from our direct out-node children
			with(codeWriter) {
				// Retrieve all the out-nodes
				val outNodes = getOutNodes()
				// Then count how many bytes that will be sent
				// Audio and signal data will always have a constant size
				val byteCountStatic = (
					outNodes.signal.size * 4 * frameSize +
						outNodes.audio.size * 4 * frameSize * channelCount
					)

				// Calculate the dynamic size of the data, which comes from Format.MIDI
				Member("int", "size = $byteCountStatic")
				for (midiOut in outNodes.midi) {
					midiOut.nodeClass.writeForEachVoice(codeWriter) {
						// Also adds 4 bytes for each outlet as int, as this describes the length of the outnode midi in bytes
						Statement("size += 4 + ${midiOut.nodeClass.writeInlet("in")}.length")
					}
				}

				// Create the output buffer byte array we will send to host program
				alloc.writeCalloc(codeWriter, "void*", "result", "size", "1")

				writeLog(codeWriter, "Size: %i", "size")

				// Create temporary buffer, used by both Format.SIGNAL and Format.AUDIO, allocating highest capacity
				Member("float", "buffer[${frameSize * channelCount}]")

				// Place the out-nodes that receives Format.SIGNAL data and lay the result into the output buffer
				for ((outNodeIndex, outNode) in outNodes.signal.withIndex()) {
					Call("memset", "buffer, 0, $frameSize * sizeof(float)")

					// Merge all the voices of this out-node
					outNode.nodeClass.writeForEachVoice(codeWriter) {
						outNode.nodeClass.writeForEachSample(codeWriter) {
							Statement("buffer[sample_index] += ${outNode.nodeClass.writeInlet("in")}.signal[sample_index]")
						}
					}

					// Copy the resulting buffer of this out-node to the output buffer
					Call(
						"memcpy",
						"result + sizeof(float) * $frameSize * $outNodeIndex, buffer, $frameSize * sizeof(float)"
					)
				}

				// Then do the out-nodes that receives Format.AUDIO data and put onto the output buffer
				for ((outNodeIndex, outNode) in outNodes.audio.withIndex()) {
					Call("memset", "buffer, 0, ${frameSize * channelCount * 4}")

					outNode.nodeClass.writeForEachVoice(codeWriter) {
						outNode.nodeClass.writeForEachChannel(codeWriter) {
							outNode.nodeClass.writeForEachSample(codeWriter) {
								Statement("buffer[sample_index + channel_index * $frameSize] += ${outNode.nodeClass.writeInlet("in")}.audio[sample_index + channel_index * $frameSize]")
							}
						}
					}

					// Copy the resulting buffer of this out-node to the output buffer
					Call(
						"memcpy",
						"result + ${frameSize * outNodes.signal.size + frameSize * outNodeIndex * 4}, buffer, ${frameSize * 4}"
					)
				}

				if (outNodes.midi.isNotEmpty()) {
					Member("int", "length")
					for (outNode in outNodes.midi) {
						Statement("length = 0")
						outNode.nodeClass.writeForEachVoice(codeWriter) {
							Statement("length += ${outNode.nodeClass.writeInlet("in")}.length")
						}

						If("length > 0") { // Only send midi output if there is any
							Member("int", "offset = $byteCountStatic") // Set offset right after Format.SIGNAL + Format.AUDIO data

							// First write the size of the midi that is coming out of current outNode
							Statement("*(int *)(result + offset) = length")

							Member("char", "temp_midi[length]")
							Call("memset", "temp_midi, 0, length")

							Member("int", "midi_index = 0")
							outNode.nodeClass.writeForEachVoice(codeWriter) {
								For("int i = 0", "i < ${outNode.nodeClass.writeInlet("in")}.length", "i++") {
									Statement("temp_midi[midi_index++] = ${outNode.nodeClass.writeInlet("in")}.messages[i]")
								}
							}

							// Then copy the result
							Call("memcpy", "result + offset + 4, temp_midi, length")

							// Move offset, plus 4 bytes of size in int
							Statement("offset += length + 4")
						}
					}
				}

				Call("send", "size, result")

				alloc.writeFree(codeWriter, "result")
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> {
		if (node.id == "temporary_top_node") {
			// When we have this name, it means that we are create temporarily by the LLVM backend itself. Do not send anything
			return listOf()
		}

		val outNodes = getOutNodes()

		val fixedLength = outNodes.signal.size * frameSize * 4 + outNodes.audio.size * 4 * frameSize * channelCount

		if (data.limit() < fixedLength)
			error("Returned data from LLVM DSP backend should have had a size of minimum $fixedLength but was ${data.limit()}")

		// Start by reading the fixed size data first
		// Signal out nodes
		val signal = HashMap<String, FloatArray>()
		for (outSignal in outNodes.signal) {
			val result = FloatArray(frameSize)
			for (i in 0 until frameSize)
				result[i] = data.float

			signal[outSignal.nodeId] = result
		}

		val audio = HashMap<String, List<FloatArray>>()
		for (outAudio in outNodes.audio) {
			val result = ArrayList<FloatArray>()
			for (channel in 0 until channelCount) {
				val channelResult = FloatArray(frameSize)
				for (i in 0 until frameSize)
					channelResult[i] = data.float

				result.add(channelResult)
			}

			audio[outAudio.nodeId] = result
		}

		// Then read the MIDI part
		val midi = HashMap<String, ShortArray>()
		for (outMidi in outNodes.midi) {
			val size = data.int
			val midiPacket = ShortArray(size)
			for (i in 0 until size) {
				val midiByte = data.get().toShort()
				if (midiByte >= 0)
					midiPacket[i] = midiByte
				else
					midiPacket[i] = (midiByte + 256).toShort()
			}
			midi[outMidi.nodeId] = midiPacket
		}

		return listOf(
			Group1OutputFrameData(
				nodeId,
				0f, // TODO send correct playback parameters
				0f, // TODO send correct playback parameters
				signal,
				audio,
				midi,
			)
		)
	}

	private class OutNodes(
		val signal: List<TranspilerNode>,
		val audio: List<TranspilerNode>,
		val midi: List<TranspilerNode>
	)

	private fun getOutNodes(): OutNodes {
		val outs = getChildren().filter { shared.nodeProperties.getName(it.node) == getName(Out::class) }
		val signalOuts = outs.filter { it.getInletType("in") == Format.SIGNAL }
			.sortedBy { it.node.id } // Note that we sort, as we don't store the order the out node data are stored
		val audioOuts = outs.filter { it.getInletType("in") == Format.AUDIO }.sortedBy { it.node.id }
		val midiOuts = outs.filter { it.getInletType("in") == Format.MIDI }.sortedBy { it.node.id }

		return OutNodes(signalOuts, audioOuts, midiOuts)
	}
}
