package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.architectures.llvm.transpilercode.ohshit
import net.merayen.elastic.backend.logicnodes.list.midi_1.Properties
import net.merayen.elastic.system.intercom.NodePropertyMessage

/**
 * Midi score.
 *
 * Allows user to write midi notes and plays them on the timeline.
 */
class Midi(nodeId: String, nodeIndex: Int) : TranspilerNode(nodeId, nodeIndex) {
	private enum class Operations {
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
		 *
		 * It does not have any timing data as MIDI_SCORE_DATA_TIMING, plays all of it per process frame.
		 */
		DIRECT_MIDI,
	}

	override val nodeClass = object : NodeClass() {
		override fun onWriteParameters(codeWriter: CodeWriter) {
			with(codeWriter) {
				// The whole score data received via MIDI_SCORE_DATA
				Member("unsigned char*", "midi_score_data")

				// 	Timing for the midi_score_data in seconds
				Member("double*", "midi_score_data_timing")

				// The last position in the midi data stream. Index of midi_score_data
				Member("unsigned int", "position")
			}
		}

		override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			with(codeWriter) {
				Statement("this->parameters.midi_score_data = 0")
				Statement("this->parameters.midi_score_data_timing = 0")
				Statement("this->parameters.position = 0")
			}
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("length == 0") {
					Return() // TODO should this be detected elsewhere before calling this data receiver?
				}

				If("*(unsigned char*)(data) == ${Operations.MIDI_SCORE_DATA.ordinal}") {
					ohshit(codeWriter, "It virks! Got MIDI_SCORE_DATA")
				}
				ElseIf("*(unsigned char*)(data) == ${Operations.MIDI_SCORE_DATA_TIMING.ordinal}") {
					ohshit(codeWriter, "It virks! Got MIDI_SCORE_DATA_TIMING")
				}
				ElseIf("*(unsigned char*)(data) == ${Operations.DIRECT_MIDI.ordinal}") {
					ohshit(codeWriter, "It virks! Got DIRECT_MIDI")
				}
			}
		}
	}

	override fun onMessage(message: NodePropertyMessage) {
		val instance = message.instance as Properties
		val eventZones = instance.eventZones
		if (eventZones != null) {
			// Midi data has been updated or it is the first transfer. We will send it to the midi node in the C backend
			val midiMessages = eventZones.getAbsoluteMidiMessages()

			// Convert midi data to our local format and send to DSP node
			sendDataToDSP(midiMessages.size * 4)  {
				it.put(Operations.MIDI_SCORE_DATA.ordinal.toByte())

				for (midiMessage in midiMessages)
					it.put(midiMessage.midi!!.map { it.toByte() }.toByteArray())
			}

			// Convert midi timing and send to DSP node
			sendDataToDSP(midiMessages.size * 8) {
				it.put(Operations.MIDI_SCORE_DATA_TIMING.ordinal.toByte())

				for (midiMessage in midiMessages)
					it.putDouble(midiMessage.start!!)
			}
		}
	}
}