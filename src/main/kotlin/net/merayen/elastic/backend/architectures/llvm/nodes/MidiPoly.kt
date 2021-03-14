package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent

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

		/**
		 * Receive the whole midi data for this node.
		 *
		 * It will get resent, everything, when user modifies a note in the MIDI score editor.
		 */
		MIDI_SCORE_DATA,

		/**
		 * Timing data for the MIDI_SCORE_DATA.
		 *
		 * In seconds.
		 */
		MIDI_SCORE_DATA_TIMING,

		/**
		 * MIDI data that is not related to the score itself.
		 *
		 * When user plays on the keyboard, that MIDI gets sent through this channel.
		 * This type of MIDI gets played on top of the MIDI_SCORE_DATA midi.
		 */
		DIRECT_MIDI,
	}

	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			with(codeWriter) {
				Member("int", "voice_multiplicator") // Voices for each key being pressed down
				Member("unsigned char*", "midi_score_data") // The whole score data received via MIDI_SCORE_DATA
				Member("double*", "midi_score_data_timing") // 	Timing for the midi_score_data in seconds
			}
		}

		override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			with(codeWriter) {
				Statement("this->parameters.voice_multiplicator = 1")
				Statement("this->parameters.midi_score_data = 0")
				Statement("this->parameters.midi_score_data_timing = 0")
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
				ElseIf("*(unsigned char*)(data) == ${Operations.MIDI_SCORE_DATA.ordinal}") {
					// TODO
				}
				ElseIf("*(unsigned char*)(data) == ${Operations.MIDI_SCORE_DATA_TIMING.ordinal}") {
					// TODO
				}
				ElseIf("*(unsigned char*)(data) == ${Operations.DIRECT_MIDI.ordinal}") {
					// TODO
				}
			}
		}
	}
}