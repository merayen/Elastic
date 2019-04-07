package net.merayen.elastic.backend.script.highlang.backends

import net.merayen.elastic.backend.script.highlang.HighlangProcessor

abstract class AbstractCompiler(protected val highlangProcessor: HighlangProcessor) {
	abstract fun compile()
	abstract fun getRuntime(): AbstractRuntime
}