package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter
import kotlin.math.min

/**
 * Kills the backend immediately with a message including the Kotlin/Java traceback where this ohshit-method got called.
 */
fun writePanic(codeWriter: CodeWriter, message: String = "", args: String = "", debug: Boolean) {
	val stackTrace = Thread.currentThread().stackTrace
	val stackTraceText = stackTrace.slice(2 until min(15, stackTrace.size)).filter { it.className.split(".").last() != "CodeWriter" }.joinToString("\\n\\t") { it.toString() }
	with(codeWriter) {
		if (debug) {
			if (message.isBlank())
				Call("fprintf", "stderr, \"FATAL ERROR\"")
			else
				Call("fprintf", "stderr, \"FATAL ERROR: $message\\n\"" + (if (args.isNotBlank()) ", $args" else ""))

			Call("fprintf", "stderr, \"\\t$stackTraceText\\n\"")
			Call("fflush", "stderr")
		}
		Call("exit", "150")
	}
}