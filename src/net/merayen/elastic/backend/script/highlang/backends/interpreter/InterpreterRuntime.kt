package net.merayen.elastic.backend.script.highlang.backends.interpreter

import net.merayen.elastic.backend.script.highlang.CodeBlock
import net.merayen.elastic.backend.script.highlang.Token
import net.merayen.elastic.backend.script.highlang.backends.AbstractRuntime
import java.lang.RuntimeException

class InterpreterRuntime(private val token: Token) : AbstractRuntime() {
	override fun run() {
		if (token !is CodeBlock)
			throw RuntimeException("Topmost token must be a CodeBlock to run")


	}
}