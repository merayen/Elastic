package net.merayen.elastic.backend.architectures.llvm.nodes

import net.merayen.elastic.backend.architectures.llvm.ports.Midi
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.logicnodes.list.midi_1.DirectMidiMessage
import net.merayen.elastic.backend.logicnodes.list.midi_1.Properties
import net.merayen.elastic.system.intercom.NodeDataMessage
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
		 * The first 3 bytes, and int, describes the size of the data. The second part if the midi part itself, then the
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

				// The whole score data received via MIDI_SCORE_DATA (midi message 3 bytes, and then 8 bytes double of beat position)
				Member("unsigned char*", "midi_score_data")

				// The last position in the midi data stream. Index of midi_score_data
				Member("unsigned int", "position")

				// Direct midi data that will be played in next frame
				Member("unsigned char*", "direct_midi")

				// Length of the direct midi data, in bytes
				Member("int", "direct_midi_length")
			}
		}

		override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			with(codeWriter) {
				Statement("this->parameters.midi_score_count = 0")
				Statement("this->parameters.midi_score_data = 0")
				Statement("this->parameters.position = 0")
				Statement("this->parameters.direct_midi = NULL")
				Statement("this->parameters.direct_midi_length = 0")
			}
		}

		override fun onWriteDataReceiver(codeWriter: CodeWriter) {
			with(codeWriter) {
				If("length == 0") {
					Return() // TODO should this be detected elsewhere before calling this data receiver?
				}

				If("*(unsigned char*)(data) == ${Operations.MIDI_SCORE_DATA.ordinal}") {
					Statement("int count = (length - 1) / (3 + 8)")

					If("this->parameters.midi_score_count != 0") {
						alloc.writeFree(codeWriter, "this->parameters.midi_score_data")
					}

					Statement("unsigned char* midi_score_data = malloc(count * (3 + 8))")

					Call("memcpy", "midi_score_data, data + 1, count * (3 + 8)")

					Statement("this->parameters.midi_score_count = count")
					Statement("this->parameters.midi_score_data = midi_score_data")
				}
				ElseIf("*(unsigned char*)(data) == ${Operations.DIRECT_MIDI.ordinal}") {
					If("this->parameters.direct_midi != NULL") {
						alloc.writeFree(codeWriter, "this->parameters.direct_midi")
					}
					alloc.writeMalloc(codeWriter, "this->parameters.direct_midi", "length - 1")
					Statement("this->parameters.direct_midi_length = length - 1")

					Call("memcpy", "this->parameters.direct_midi, data + 1, length - 1")
				}
			}
		}

		override fun onWriteProcess(codeWriter: CodeWriter) {
			with(codeWriter) {

				// Send direct midi, if any, and clean up the buffer afterwards
				writeForEachVoice(codeWriter) {
					val messagesVariable = "${writeOutlet("out")}.messages"

					// Clean up outputs
					If("$messagesVariable != NULL") {
						alloc.writeFree(codeWriter, "$messagesVariable")
						Statement("$messagesVariable = NULL")
					}

					If("this->parameters.direct_midi != NULL") { // There is direct MIDI available
						if (debug) {
							If("this->parameters.direct_midi_length < 1") {
								writePanic(codeWriter, "Direct midi length must be positive")
							}
						}

						// TODO merge with the usual MIDI_SCORE_DATA midi data, so that we send that onto our outlet too
						writeForEachVoice(codeWriter) {
							Midi(frameSize, debug).cClass.writePrepare(codeWriter, writeOutlet("out"), "this->parameters.direct_midi_length")

							Call(
								"memcpy",
								"${writeOutlet("out")}.messages, this->parameters.direct_midi, this->parameters.direct_midi_length"
							)
						}

						alloc.writeFree(codeWriter, "this->parameters.direct_midi")
						Statement("this->parameters.direct_midi = NULL")
						Statement("this->parameters.direct_midi_length = 0")
					}
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
			sendDataToDSP(1 + midiMessages.size * 3 + midiMessages.size * 8) {
				it.put(Operations.MIDI_SCORE_DATA.ordinal.toByte())

				// First write all the midi messages that are 3 bytes each
				for (midiMessage in midiMessages) {
					val midi = midiMessage.midi!!

					if (midi.size != 3)
						error("All MIDI messages should be exactly 3 bytes") // We might want to leave this philosophy. SysEx does not enforce 3 byte messages

					for (x in midi)
						it.put(x.toByte())

					// Then write the beat this message is at
					it.putDouble(midiMessage.start!!)
				}
			}
		}
	}

	override fun onMessage(message: NodeDataMessage) {
		when (message) {
			is DirectMidiMessage -> {
				sendDataToDSP(1 + message.midi.size) {
					it.put(Operations.DIRECT_MIDI.ordinal.toByte())

					for (midi in message.midi)
						it.put(midi.toByte())
				}
			}
		}
	}
}