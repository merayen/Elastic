package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.getName
import net.merayen.elastic.backend.architectures.llvm.ports.Midi
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.logicnodes.Format
import net.merayen.elastic.backend.logicnodes.list.midi_poly_1.Properties
import net.merayen.elastic.backend.midi.MidiStatuses
import net.merayen.elastic.system.intercom.NodePropertyMessage

/**
 * Has 1 midi input, triggering voices on children nodes whenever a key is pressed down.
 *
 * Allows for creating several voices for each key pressed down to create full and wide sounds.
 */
class MidiPoly(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex), GroupInterface {
	private enum class Operations {
		/**
		 * How many voices to create for each key being pressed down.
		 *
		 * Increase number to create wide sounds.
		 */
		VOICE_MULTIPLICATOR,
	}

	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			with(codeWriter) {
				Member("int", "voice_multiplicator") // Voices for each key being pressed down
			}
		}

		override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			with(codeWriter) {
				Statement("this->parameters.voice_multiplicator = 1")
			}
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("length == 0") {
					Return() // TODO should this be detected elsewhere before calling this data receiver?
				}
				If("*(unsigned char*)(data) == ${Operations.VOICE_MULTIPLICATOR.ordinal} && length == 2") {
					Statement("${writeOuterParameterVariable("voice_multiplicator")} = *(unsigned char*)(data+1)")
				}
				Else {
					writePanic(codeWriter, "Invalid operation")
				}
			}
		}

		override fun onWritePreprocess(codeWriter: CodeWriter) {
			if (getInletType("in") == Format.MIDI) {
				writeForEachVoice(codeWriter) {
					with(codeWriter) {
						val midiPort = getPortStruct("in") as Midi
						midiPort.cClass.writeForEachMidiByte(codeWriter, writeInlet("in")) {
							If("midi_status == ${MidiStatuses.KEY_DOWN}") {
								writeVoiceCreation(codeWriter)
							}
						}
					}
				}
			}
		}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			// TODO support labeling of internal out-nodes and forward them to the output ports with the same name
			val outputPorts = getOutputPorts()

			if (outputPorts.size != 1)
				TODO("add support for dynamic output port count (forwarding from multiple out-nodes children)")

			val childrenOutNodes = getChildren().filter { shared.nodeProperties.getName(it.node) == getName(Out::class) }

			if (childrenOutNodes.size != 1)
				TODO("add support for dynamic out node count and forwarding. Got ${childrenOutNodes.size} Out children nodes")

			val outNode = childrenOutNodes[0]
			val outNodePortFormat = outNode.getInletType("in") ?: TODO("Allow out-nodes to have on inputs")

			with(codeWriter) {
				when (outNodePortFormat) {
					Format.SIGNAL -> {
						if (getOutletType("out") != Format.SIGNAL)
							error("Out node child has SIGNAL format while midi_poly's out has another format")

						Member("float", "result[$frameSize]")
						Call("memset", "result, 0, $frameSize * sizeof(float)")

						// Sum all the voices for the out-node into our output buffer
						outNode.nodeClass.writeForEachVoice(codeWriter) { // FIXME more intelligently add voices...? respect hierarchy?
							outNode.nodeClass.writeForEachSample(codeWriter) {
								Statement("result[sample_index] += ${outNode.nodeClass.writeInlet("in")}.signal[sample_index]")
							}
						}

						// Then copy our output buffer to out output port
						writeForEachVoice(codeWriter) {
							Call("memcpy", "${writeOutlet("out")}.signal, result, $frameSize * sizeof(float)")
						}
					}
					else -> TODO("add support forwarding port format '${outNodePortFormat.name}'")
				}
			}
		}
	}

	override fun onMessage(message: NodePropertyMessage) {
		val instance = message.instance as Properties
		val midiScoreData = instance.midiScoreData
		val midiScoreDataTiming = instance.midiScoreDataTiming

		if (midiScoreData != null) {
			TODO()
		}
		if (midiScoreDataTiming != null) {
			TODO()
		}
	}
}