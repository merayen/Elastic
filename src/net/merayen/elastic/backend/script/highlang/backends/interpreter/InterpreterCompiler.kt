package net.merayen.elastic.backend.script.highlang.backends.interpreter

import net.merayen.elastic.backend.script.highlang.Token
import net.merayen.elastic.backend.script.highlang.backends.AbstractCompiler

class InterpreterCompiler(token: Token) : AbstractCompiler(token) {
	override fun getRuntime() = InterpreterRuntime(token)

	override fun compile() {

	}
}