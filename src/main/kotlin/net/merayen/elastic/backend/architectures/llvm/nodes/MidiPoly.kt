package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.logicnodes.list.midi_poly_1.Properties
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
			}
		}
	}

	override fun onMessage(message: NodePropertyMessage) {
		val instance = message.instance as Properties
		if (instance.midiScoreData != null) {
			TODO()
		}
		if (instance.midiScoreDataTiming != null) {
			TODO()
		}
	}
}