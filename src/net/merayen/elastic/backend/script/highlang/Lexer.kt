package net.merayen.elastic.backend.script.highlang

import java.util.ArrayDeque
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class Lexer(var source: String) {
	var cursor = 0
		private set

	val result: CodeBlock = CodeBlock(this)

	private var tokenStack = ArrayDeque<Token>()

	init {
		source = preparse(source)
		tokenStack.add(result)
		result.onExecute()
	}

	fun consume(text: String): String? {
		if (source.startsWith(text, cursor)) {
			cursor += text.length
			return text
		}

		return null
	}

	fun consume(textArray: Array<String>): String? {
		for (x in textArray)
			if (consume(x) != null)
				return x
		return null
	}

	fun consume(regex: Regex): String? {
		val result = regex.find(source.substring(cursor)) ?: return null

		if (result.value.length > 0) {
			cursor += result.value.length
			return result.value
		}

		return null
	}

	fun consume(token: KClass<out Token>): Token? {
		val tokenInstance = token.primaryConstructor?.call(this) ?: return null
		val lastPosition = cursor

		tokenStack.add(tokenInstance)
		val result = tokenInstance.onExecute()
		tokenStack.removeLast()

		if (result) {
			tokenInstance.parent = tokenStack.last
			return tokenInstance
		} else {
			cursor = lastPosition
			return null
		}
	}

	fun currentLine() = source.split("\n")[currentLineNumber() - 1]
	fun currentLineNumber() = source.substring(0, cursor).count { it == '\n' } + 1
	fun currentLinePosition() = cursor - source.substring(0, cursor + 1).indexOfLast { it == '\n' }
}


/**
 * Changes the source to be easier to parse with tokens.
 * Adds "{" and "}"
 */
private fun preparse(text: String): String {
	val lines = text.trimMargin()
			.split("\n")
			// Removes comments in preparse-stage, as the "#"-character isn't used for anything else (no support for strings)
			.asSequence()
			.map { if (it.contains("#")) it.substring(0, it.indexOf("#")) else it }
			.toMutableList()
	lines.add("")

	var depth = 0

	for (line in 0 until lines.size) {
		var level = 0
		for (i in 0 until lines[line].length) {
			if (lines[line][i] != '\t')
				break
			level++
		}

		if(level > depth) {
			lines[line - 1] += " " + "{".repeat(level - depth)
			depth = level
		} else if(level < depth) {
			lines[line] = "\t".repeat(level) + "}".repeat(depth - level) + " " + lines[line].trim()
			depth = level
		}
	}

	return "{" + lines.joinToString("\n") + "}"
}
