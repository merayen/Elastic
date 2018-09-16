package net.merayen.elastic.backend.script.highlang

import kotlin.reflect.KClass

abstract class Token(private val lexer: Lexer) {
	var parent: Token? = null
		set(value) {
			if (value != null)
				value.children.add(this)
			else
				parent?.children?.remove(this)

			field = value
		}

	val children = ArrayList<Token>()

	/**
	 * Returns true if the text after from the current cursor is compatible with the token.
	 */
	abstract fun onExecute(): Boolean

	open fun onToString(): Array<Any?> = arrayOf()

	protected fun consume(text: String) = lexer.consume(text)
	protected fun consume(token: KClass<out Token>) = lexer.consume(token)
	protected fun consume(regex: Regex) = lexer.consume(regex)

	protected fun startsWith(string: String) = lexer.source.startsWith(string)
	protected val position: Int
		get() = lexer.cursor

	protected fun debug() = println("DEBUG: " + lexer.currentLine())

	override fun toString() = this::class.simpleName + "(" + onToString().joinToString(", ") + ")"

	fun syntaxError(description: String) {
		throw RuntimeException(
				"Line ${lexer.currentLineNumber()}, position ${lexer.currentLinePosition()}: $description\n" +
				lexer.currentLine() + "\n" + (" ".repeat(lexer.currentLinePosition() - 1) + '^')
		)
	}
}


/*class BlockContinuation(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume(Whitespace::class)

	}

}*/


class VariableDeclaration(lexer: Lexer) : Token(lexer) {
	override fun onToString() = arrayOf<Any?>()

	override fun onExecute(): Boolean {
		consume(Whitespace::class)
		if (consume("var ") != null) {
			if (consume(Variable::class) != null) {
				if (consume(EqualityOperator::class) != null) {
					val number = consume(Number::class)
					if (number == null)
						throw RuntimeException("No!")
					return true
				} else {
					return true
				}
			} else {
				syntaxError("There must be a variable name after 'var'")
			}
		}

		return false
	}
}


class Variable(lexer: Lexer) : Token(lexer) {
	var name: String? = null
		private set

	override fun onExecute(): Boolean {
		name = consume(Regex("[a-zA-Z]+"))
		return name != null
	}
}


class VariableOperation

/*class EndLine(lexer: Lexer): Token(lexer) {
	override fun onExecute(): Boolean {
		consume(Whitespace::class)
		if (consume("\n") != null)
			return true

		return true
	}

}*/


/**
 * Eats all whitespaces
 */
class Whitespace(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		var lastPosition = position
		while (true) {
			//consume(Regex("^ +"))
			// TODO optimize?
			consume(" ")
			//consume("\n")
			consume("\t")
			consume("\r")

			if (lastPosition == position)
				break

			lastPosition = position
		}

		return false // Never an interest to store whitespaces
	}
}


/**
 * A tab indent, meaning a code block
 */
class CodeBlock(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("{") == null)
			return false // Not a CodeBlock

		var lastPosition = position
		while (true) {
			consume(Statement::class)

			if (consume("}") != null)
				break // End of CodeBlock

			if (lastPosition == position)
				syntaxError("EOF. Expected '}'")

			lastPosition = position
		}

		return true // ???
	}
}


class Statement(lexer: Lexer) : Token(lexer) {
	private val possible = arrayOf(
			VariableDeclaration::class,

	)

	override fun onExecute(): Boolean {
		consume(Whitespace::class)
		for (tokenClass in possible) {
			val token = consume(tokenClass)
			if (token != null)
				return true
		}

		if (consume("\n") == null)
			syntaxError("Expected end of line")

		return false
	}
}


class Number(lexer: Lexer) : Token(lexer) {
	var number: Float? = null

	override fun onExecute(): Boolean {
		consume(Whitespace::class)

		var num = consume(Regex("^[0-9]+.[0-9]+"))
		if (num == null)
			num = consume(Regex("^[0-9]+"))

		if (num != null) {
			number = num.toFloat()
			return true
		}

		return false
	}

	override fun onToString() = arrayOf<Any?>(number)
}


class EqualityOperator(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume(Whitespace::class)
		return consume("=") != null
	}
}


/*class FunctionCall(lexer: Lexer) : Token(lexer) {
	override fun onExecute() {
		val variable = consume(Variable::class)
	}
}


class For(lexer: Lexer) : Token(lexer) {
	override fun onExecute() {
		consume("for ")
	}
}*/