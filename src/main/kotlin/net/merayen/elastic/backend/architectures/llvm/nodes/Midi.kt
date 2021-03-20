package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.logicnodes.list.midi_1.Properties
import net.merayen.elastic.system.intercom.NodePropertyMessage
import javax.swing.plaf.nimbus.State

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
		 * The first 4 bytes, and int, describes the size of the data. The second part if the midi part itself, then the
		 * timing as doubles, 8 bytes for each.
		 *
		 * It will get resent, everything, when user modifies a note in the MIDI score editor.
		 */
		MIDI_SCORE_DATA,

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
				Member("int", "midi_score_count")
				// The whole score data received via MIDI_SCORE_DATA (midi message 4 bytes, and then 8 bytes double of beat position)
				Member("unsigned char*", "midi_score_data")

				// The last position in the midi data stream. Index of midi_score_data
				Member("unsigned int", "position")
			}
		}

		override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			with(codeWriter) {
				Statement("this->parameters.midi_score_count = 0")
				Statement("this->parameters.midi_score_data = 0")
				Statement("this->parameters.position = 0")
			}
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("length == 0") {
					Return() // TODO should this be detected elsewhere before calling this data receiver?
				}

				If("*(unsigned char*)(data) == ${Operations.MIDI_SCORE_DATA.ordinal}") {
					Statement("int count = (length - 1) / (4 + 8)")

					If("this->parameters.midi_score_count != 0") {
						Call("free", "this->parameters.midi_score_data")
					}

					Statement("unsigned char* midi_score_data = malloc(count * (4 + 8))")

					Call("memcpy", "midi_score_data, data + 1, count * (4 + 8)")

					Statement("this->parameters.midi_score_count = count")
					Statement("this->parameters.midi_score_data = midi_score_data")
				}
				ElseIf("*(unsigned char*)(data) == ${Operations.DIRECT_MIDI.ordinal}") {
					writePanic(codeWriter, "It virks! Got DIRECT_MIDI")
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
			sendDataToDSP(1 + midiMessages.size * 4 + midiMessages.size * 8) {
				it.put(Operations.MIDI_SCORE_DATA.ordinal.toByte())

				// First write all the midi messages that are 4 bytes each
				for (midiMessage in midiMessages) {
					val midi = midiMessage.midi!!
					// First write the midi message itself, always at 4 byte length
					for (i in 0 until 4)
						if (midi.size > i)
							it.put(midi[i].toByte())
						else
							it.put(0)

					// Then write the beat this message is at
					it.putDouble(midiMessage.start!!)
				}
			}
		}
	}
}