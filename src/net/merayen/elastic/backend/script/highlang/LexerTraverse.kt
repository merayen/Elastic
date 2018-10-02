package net.merayen.elastic.backend.script.highlang

import kotlin.collections.ArrayList

class LexerTraverse(private val inToken: Token) {
	/*class Path : ArrayList<Token>() {

	}*/

	class DoNotTraverse

	fun allChildren() = allChildren(inToken)

	private fun allChildren(token: Token): ArrayList<Token> {
		val result = ArrayList<Token>()

		for (child in token.children) {
			result.add(child)
			result.addAll(allChildren(child))
		}

		return result
	}

	fun walk(func: (Array<Token>, Token) -> Boolean) {
		walk(inToken, func)
	}

	private fun walk(token: Token, func: (Array<Token>, Token) -> Boolean) {
		if(func(getPath(token).toTypedArray(), token))
			for (visitToken in allChildren())
				walk(visitToken, func)

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