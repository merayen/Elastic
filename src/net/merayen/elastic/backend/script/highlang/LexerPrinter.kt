package net.merayen.elastic.backend.script.highlang

class LexerPrinter(private val traverse: LexerTraverse, private val token: Token? = null) {
	override fun toString(): String {
		var result = ""
		traverse.walk() { path, token ->
			result += "\t".repeat(path.size) + "${token}\n"
		}
		return result
	}
}