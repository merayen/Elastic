package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

/**
 * Logs stuff, using a mutex.
 */
class LogComponent {
	val fprintfMutex = PThreadMutex("fprintf_mutex", this, false)

	fun writeDefinition(codeWriter: CodeWriter) {
		fprintfMutex.writeDefinition(codeWriter)
	}

	fun write(codeWriter: CodeWriter, text: String, args: String? = null) {
		with(codeWriter) {
			fprintfMutex.writeLock(codeWriter) {
				Call("fprintf", "stderr, \"DSP[line=%i, thread=%ld]: \", @LINENUMBER@ - 4, pthread_self()")
				Call("fprintf", "stderr, \"$text\\n\"${if (args != null && args.isNotBlank()) ", $args" else ""}")
				Call("fflush", "stderr")
			}
		}
	}
}