package net.merayen.elastic.backend.script.highlang

class LexerOptimizer(val lexerTraverse: LexerTraverse) {
	fun removeNoOpTokens() {
		val noOpTokens = lexerTraverse.allChildren().filter { Whitespace::class.isInstance(it) }

		for (token in noOpTokens) {
			token.parent = null
		}
	}
}