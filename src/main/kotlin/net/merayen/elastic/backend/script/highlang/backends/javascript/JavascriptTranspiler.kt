package net.merayen.elastic.backend.script.highlang.backends.javascript

import net.merayen.elastic.backend.script.highlang.CodeBlock
import net.merayen.elastic.backend.script.highlang.HighlangProcessor
import net.merayen.elastic.backend.script.highlang.LexerTraverse
import net.merayen.elastic.backend.script.highlang.backends.AbstractCompiler
import net.merayen.elastic.backend.script.highlang.backends.AbstractRuntime

class JavascriptTranspiler(private val processor: HighlangProcessor) : AbstractCompiler(processor) {
	override fun compile() {
		val result = StringBuilder()

		val lt = LexerTraverse(processor.token)
		lt.walk { arrayOfTokens, token ->
			println("${arrayOfTokens.joinToString { "$it-" }} [$token]")
			when (token) {
				is CodeBlock -> {
					result.append("(function(){")
				}
			}
			true
		}
	}

	override fun getRuntime(): AbstractRuntime {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

}