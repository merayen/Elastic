package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

abstract class TranspilerComponent(protected val logComponent: LogComponent, protected val debug: Boolean) {
	protected fun writeLog(codeWriter: CodeWriter, text: String, args: String? = null) {
		if (debug)
			logComponent.write(codeWriter, text, args)
	}

	protected fun panic(codeWriter: CodeWriter, message: String = "", args: String = "") {
		writePanic(codeWriter, message, args, debug)
	}
}