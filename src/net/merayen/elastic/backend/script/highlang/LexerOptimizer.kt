package net.merayen.elastic.backend.script.highlang

class LexerOptimizer(val lexerTraverse: LexerTraverse) {
	fun removeNoOpTokens() {
		val tokens = lexerTraverse.allChildren()

		for (token in tokens.filter { Whitespace::class.isInstance(it) })
			token.parent = null

		for (token in tokens.filter { EmptySpace::class.isInstance(it) })
			token.parent = null
	}
}