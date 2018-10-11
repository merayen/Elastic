package net.merayen.elastic.backend.script.highlang.backends

import net.merayen.elastic.backend.script.highlang.Token

abstract class AbstractCompiler(protected val token: Token) {
	abstract fun compile()
	abstract fun getRuntime(): AbstractRuntime
}