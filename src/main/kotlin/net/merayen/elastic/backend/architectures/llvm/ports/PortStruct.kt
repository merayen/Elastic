package net.merayen.elastic.backend.architectures.llvm.ports

import net.merayen.elastic.backend.architectures.llvm.templating.CClass
import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

abstract class PortStruct(protected val frameSize: Int, protected val debug: Boolean) {
	abstract val cClass: CClass

	protected fun writePanic(codeWriter: CodeWriter, message: String = "", args: String = "") {
		net.merayen.elastic.backend.architectures.llvm.transpilercode.writePanic(codeWriter, message, args, debug)
	}
}