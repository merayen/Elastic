package net.merayen.elastic.backend.architectures.llvm.nodes

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

		override fun onWriteProcess(codeWriter: CodeWriter) {
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
			// TODO then run process() on all our children node ourself...? Instead of having DependencyList do it? Perhaps we run a process_workunit_123()?
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