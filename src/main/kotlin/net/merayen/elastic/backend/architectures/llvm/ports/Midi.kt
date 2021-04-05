package net.merayen.elastic.backend.architectures.llvm.ports

import net.merayen.elastic.backend.architectures.llvm.templating.CClass
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent
import net.merayen.elastic.backend.midi.MidiStatuses

/**
 * Midi Outlet.
 *
 * TODO support time codes
 */
class Midi(frameSize: Int, debug: Boolean) : PortStruct(frameSize, debug) {
	inner class Class : CClass("PortDataMidi") {
		override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			super.onWriteInit(codeWriter, allocComponent)
			with(codeWriter) {
				//Statement("this->capacity = 0")
				Statement("this->length = 0")
				Statement("this->messages = NULL")
			}
		}

		override fun onWriteDestroy(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			super.onWriteDestroy(codeWriter, allocComponent)
			with(codeWriter) {
				If("this->messages != NULL") {
					if (allocComponent != null)
						allocComponent.writeFree(codeWriter, "this->messages")
					else
						Call("free", "this->messages")
				}
			}
		}

		override fun onWriteMethods(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			// Method that prepares this outlet
			addInstanceMethod(codeWriter, "void", "prepare", "int length") {
				codeWriter.If("this->messages != NULL") {
					if (allocComponent != null)
						allocComponent.writeFree(codeWriter, "this->messages")
					else
						codeWriter.Call("free", "this->messages")
				}
				if (allocComponent != null)
					allocComponent.writeMalloc(codeWriter, "", "this->messages", "length")
				else
					codeWriter.Statement("this->messages = malloc(length, sizeof(unsigned char))")
				codeWriter.Statement("this->length = length")
			}
		}

		override fun onWriteMembers(codeWriter: CodeWriter) {
			//codeWriter.Member("int", "capacity")
			codeWriter.Member("int", "length") // Length of the buffer
			codeWriter.Member("unsigned char*", "messages")
		}

		/**
		 * Writes code that initializes midi outlet.
		 *
		 * @param lengthExpression How many bytes the midi output is
		 */
		fun writePrepare(codeWriter: CodeWriter, instanceExpression: String, lengthExpression: String) {
			codeWriter.Call("${this.name}_prepare", "&$instanceExpression, $lengthExpression")
		}

		/**
		 * Write a for-loop for each midi message in the loop.
		 *
		 * Available parameters in C code:
		 *
		 * midi_index - Current position in the `messages` variable
		 *
		 * midi_length - The length of the packet (if ordinary midi message, always 3 bytes, while SysEx has variable length)
		 *
		 * midi_status - The midi status byte. Use it to figure out what type of midi command this is
		 */
		fun writeForEachMidiByte(codeWriter: CodeWriter, instanceExpression: String, block: () -> Unit) {
			with(codeWriter) {
				For("int midi_index = 0", "midi_index < $instanceExpression.length", "midi_index++") {
					Member("unsigned char", "midi_status = ${instanceExpression}.messages[midi_index]")
					Member("int", "midi_length")
					If("midi_status == ${MidiStatuses.KEY_DOWN}") {
						Statement("midi_length = 3")
						Block(func = block)
					}
					// Add SysEx sometime in the future?
					Else {
						writePanic(codeWriter, "Unknown MIDI status %d", "(int)midi_status")
					}
					Statement("midi_index += midi_length")
				}
			}
		}
	}

	override val cClass = Class()
}