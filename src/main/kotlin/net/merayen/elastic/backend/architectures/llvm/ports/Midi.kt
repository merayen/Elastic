package net.merayen.elastic.backend.architectures.llvm.ports

import net.merayen.elastic.backend.architectures.llvm.templating.CClass
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent

/**
 * Midi Outlet.
 *
 * TODO support time codes
 */
class Midi(frameSize: Int) : PortStruct(frameSize) {
	override val clsName = "PortDataMidi"

	inner class Class :  CClass(clsName) {
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
		 * @param length How many bytes the midi output is
		 */
		fun writePrepare(codeWriter: CodeWriter, instanceExpression: String, lengthExpression: String) {
			codeWriter.Call("${this.name}_prepare", "&$instanceExpression, $lengthExpression")
		}
	}

	override val cClass = Class()
}