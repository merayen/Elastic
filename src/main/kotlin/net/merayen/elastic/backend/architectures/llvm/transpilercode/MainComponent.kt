package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

class MainComponent(private val fprintfMutex: PThreadMutex, log: LogComponent, debug: Boolean) : TranspilerComponent(log, debug) {
	fun create(codeWriter: CodeWriter) {
		with(codeWriter) {
			Method("int", "main") {
				fprintfMutex.writeInit(codeWriter)

				Call("init_threads")
				Call("init_workunits")
				Call("init_stdinout")

				//Statement("bool wait_for_debug = true")
				//While("wait_for_debug") {}

				Call("init_nodes")
				Call("init_voice")

				For("unsigned long i = 0", "", "i++") {
					Call("process_communication")
					Call("process")
					writeLog(codeWriter, "Done with frame %lu", "i")
				}

				Return("0")
			}
		}
	}
}