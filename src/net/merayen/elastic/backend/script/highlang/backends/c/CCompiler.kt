package net.merayen.elastic.backend.script.highlang.backends.c

import net.merayen.elastic.backend.script.highlang.HighlangProcessor
import net.merayen.elastic.backend.script.highlang.Token
import net.merayen.elastic.backend.script.highlang.backends.AbstractCompiler

class CCompiler(highlangProcessor: HighlangProcessor) : AbstractCompiler(highlangProcessor) {
	override fun getRuntime() = CRuntime()

	override fun compile() {

	}
}