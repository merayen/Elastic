package net.merayen.elastic.backend.script.highlang

import kotlin.collections.ArrayList

class LexerTraverse(private val lexer: Lexer) {
	fun allChildren(inToken: Token? = null): ArrayList<Token> {
		val result = ArrayList<Token>()

		val token: Token
		if (inToken == null) {
			result.add(lexer.result)
			token = lexer.result
		} else {
			token = inToken
		}

		for (child in token.children) {
			result.add(child)
			result.addAll(allChildren(child))
		}

		return result
	}

	fun walk(token: Token? = null, func: (Array<Token>, Token) -> Unit) {
		//val path = ArrayDeque<Token>()

		for (visitToken in allChildren(token)) {
			val path = getPath(visitToken)

			// Not working
			/*if (visitToken.parent == null)
				// Topmost. Ok!
			else if (path.size > 0 && visitToken.parent == path.last)
				// Still same parent
			else if (path.size == 0 || visitToken.parent?.parent == path.last)
				path.add(visitToken.parent) // New parent
			else
				while (path.size > 0 && path.removeLast() != visitToken.parent);*/

			func(path.toTypedArray(), visitToken)
		}
	}

	private fun getPath(token: Token): ArrayList<Token> {
		val path = ArrayList<Token>()

		var t = token
		while (true) {
			val parent = t.parent
			if (parent != null) {
				t = parent
				path.add(t)
			} else {
				break
			}
		}

		return path
	}
}