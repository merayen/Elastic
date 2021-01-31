package net.merayen.elastic.backend.architectures.llvm.ports

import net.merayen.elastic.backend.architectures.llvm.templating.CClass
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import net.merayen.elastic.backend.architectures.llvm.transpilercode.AllocComponent

class Midi(frameSize: Int) : PortStruct(frameSize) {
	override val clsName = "PortDataMidi"

	override val cClass = object : CClass(clsName) {
		override fun onWriteInit(codeWriter: CodeWriter, allocComponent: AllocComponent?) {
			super.onWriteInit(codeWriter, allocComponent)
			with(codeWriter) {
				Statement("this->capacity = 0")
				Statement("this->count = 0")
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
			addInstanceMethod(codeWriter, "void", "prepare", "int count") {
				codeWriter.If("this->count < count") {
					codeWriter.If("this->messages != NULL") {
						if (allocComponent != null)
							allocComponent.writeFree(codeWriter, "this->messages")
						else
							codeWriter.Call("free", "this->messages")
					}
					if (allocComponent != null)
						allocComponent.writeCalloc(codeWriter, "", "this->messages", "3 * count", "sizeof(unsigned char)")
					else
						codeWriter.Statement("this->messages = calloc(3 * count, sizeof(unsigned char))")
				}
				codeWriter.Statement("this->count = count")
			}
		}

		override fun onWriteMembers(codeWriter: CodeWriter) {
			codeWriter.Member("int", "capacity")
			codeWriter.Member("int", "count")
			codeWriter.Member("unsigned char*", "messages")
		}
	}
}