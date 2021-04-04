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
class Group(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex), GroupInterface {
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

				// Calculate the dynamic size of the data, which comes from midi
				Member("int", "size = $byteCountStatic")
				for (midiOut in outNodes.midi) {
					midiOut.nodeClass.writeForEachVoice(codeWriter) {
						Statement("size += ${midiOut.nodeClass.writeInlet("in")}.length")
					}
				}

				Member("void*", "result")
				alloc.writeMalloc(codeWriter, "result", "size")

				writeLog(codeWriter, "Size: %i", "size")

				// Copy signal data to output buffer
				for ((i, signalOut) in outNodes.signal.withIndex()) {
					signalOut.nodeClass.writeForEachVoice(codeWriter) {
						fortsett_her()
						Call( // TODO wrong! you need to sum all the out node voices...
							"memcpy",
							"result + $frameSize * sizeof(float) * $i, ${signalOut.nodeClass.writeInlet("in")}.signal, $frameSize"
						)
					}
				}

				for ((i, audioOut) in outNodes.audio.withIndex()) {
					TODO("support forwarding audio out nodes")
				}

				for ((i, midiOut) in outNodes.midi.withIndex()) {
					TODO("support forwarding midi out nodes")
				}

				Call("send", "size, result")

				alloc.writeFree(codeWriter, "result")
			}
		}
	}

	override fun onDataFromDSP(data: ByteBuffer): List<NodeDataMessage> {
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
		for (outMidi in outNodes.midi)
			TODO("make group node forward midi too")

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
		val signalOuts = outs.filter { it.getInletType("in") == Format.SIGNAL }.sortedBy { it.node.id }
		val audioOuts = outs.filter { it.getInletType("in") == Format.AUDIO }.sortedBy { it.node.id }
		val midiOuts = outs.filter { it.getInletType("in") == Format.MIDI }.sortedBy { it.node.id }

		return OutNodes(signalOuts, audioOuts, midiOuts)
	}
}