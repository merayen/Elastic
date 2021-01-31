package net.merayen.elastic.backend.architectures.llvm.transpilercode

import net.merayen.elastic.backend.architectures.llvm.templating.CodeWriter

class MainComponent(private val fprintfMutex: PThreadMutex, private val log: LogComponent, private val debug: Boolean) {
	fun create(codeWriter: CodeWriter) {
		with(codeWriter) {
			Method("int", "main") {
				fprintfMutex.writeInit(codeWriter)

				Call("init_threads")
				Call("init_workunits")
				Call("init_stdinout")
				Call("init_nodes")
				Call("init_voice")

				For("int i = 0", "", "i++") {
					Call("process_communication")
					Call("process")
					if (debug) log.write(codeWriter, "Done with frame %i", "i")
				}

				Return("0")
			}
		}
	}
}