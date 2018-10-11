package net.merayen.elastic.backend.script.highlang.backends.c

import net.merayen.elastic.backend.script.highlang.Token
import net.merayen.elastic.backend.script.highlang.backends.AbstractCompiler

class CCompiler(token: Token) : AbstractCompiler(token) {
	override fun getRuntime() = CRuntime()

	override fun compile() {

	}
}