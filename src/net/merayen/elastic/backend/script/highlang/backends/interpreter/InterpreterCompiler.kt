package net.merayen.elastic.backend.script.highlang.backends.interpreter

import net.merayen.elastic.backend.script.highlang.HighlangProcessor
import net.merayen.elastic.backend.script.highlang.Token
import net.merayen.elastic.backend.script.highlang.backends.AbstractCompiler

class InterpreterCompiler(highlangProcessor: HighlangProcessor) : AbstractCompiler(highlangProcessor) {
	override fun getRuntime() = InterpreterRuntime(highlangProcessor)

	override fun compile() {

	}
}