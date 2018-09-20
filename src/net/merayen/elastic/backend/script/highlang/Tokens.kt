package net.merayen.elastic.backend.script.highlang

import kotlin.math.max
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
	protected val currentLine: String
		get() = lexer.currentLine()

	protected val position: Int
		get() = lexer.cursor

	protected fun debug() = println("DEBUG: " + lexer.currentLine() + "\n" + " ".repeat(lexer.currentLinePosition() + 6) + "^")

	override fun toString() = this::class.simpleName + "(" + onToString().joinToString(", ") + ")"

	fun syntaxError(description: String) {
		throw RuntimeException(
				"Line ${lexer.currentLineNumber()}, position ${lexer.currentLinePosition()}: $description\n" +
				lexer.currentLine() + "\n" + (" ".repeat(max(0, lexer.currentLinePosition() - 1)) + '^')
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
		name = consume(Regex("[a-zA-Z_]+"))
		return name != null
	}

	override fun onToString() = arrayOf<Any?>(name)
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

		return true
	}
}


/**
 * Consumes all whitespaces
 */
class EmptySpace(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		val initial = position
		var lastPosition = position
		while (true) {
			consume(" ")
			consume("\t")
			consume("\n")

			if (lastPosition == position)
				break

			lastPosition = position
		}

		return initial != position
	}
}


/**
 * A tab indent, meaning a code block
 */
class CodeBlock(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume(Whitespace::class)

		if (consume("{") == null)
			return false // Not a CodeBlock

		var lastPosition = position
		while (true) {
			consume(EmptySpace::class)

			consume(Statement::class)

			consume(EmptySpace::class)

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
			For::class,
			While::class,
			FunctionCall::class,
			FunctionDeclaration::class,
			Pass::class
	)

	override fun onExecute(): Boolean {
		consume(Whitespace::class)
		for (tokenClass in possible) {
			val token = consume(tokenClass)
			if (token != null) {
				consume(Whitespace::class)
				//if (consume("\n") == null)
				//	syntaxError("Expected end of line")
				return true
			}
		}

		syntaxError("Invalid statement")

		return false // Never gets here
	}
}


class Pass(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume(Whitespace::class)
		return consume("pass") != null
	}
}


class Number(lexer: Lexer) : Token(lexer) {
	var number: Float? = null

	override fun onExecute(): Boolean {
		consume(Whitespace::class)

		val num = consume(Regex("^[0-9]+\\.[0-9]+")) ?: consume(Regex("^[0-9]+"))

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


class FunctionCall(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		val variable = consume(Variable::class)
		if(variable == null)
			return false

		val args = consume(FunctionCallArguments::class)
		if(args == null)
			return false

		return true
	}
}


class FunctionCallArguments(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("(") == null)
			return false

		var lastPosition = position
		while (consume(")") == null) {
			consume(Expression::class)
			consume(Whitespace::class)
			consume(",")

			if (lastPosition == position)
				syntaxError("Invalid function arguments")

			lastPosition = position
		}

		return true
	}
}


class Expression(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume(Whitespace::class)

		when {
			consume(Number::class) != null -> {}
			consume(FunctionCall::class) != null -> {}
			consume(Variable::class) != null -> {}
			else -> syntaxError("Expected a valid expression")
		}
		return true
	}
}


class FunctionDeclaration(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("def ") == null)
			return false

		val name = consume(Variable::class)
		if (name == null)
			return false

		consume(Whitespace::class)

		if (consume(FunctionArguments::class) == null)
			return false

		if (consume(CodeBlock::class) == null)
			return false

		return true
	}
}


class FunctionArguments(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("(") == null)
			return false

		var lastPosition = position
		while (consume(")") == null) {
			consume(Whitespace::class)
			consume(Variable::class)
			consume(Whitespace::class)
			consume(",")

			if (lastPosition == position)
				syntaxError("Invalid function arguments")

			lastPosition = position
		}

		return true
	}
}


/*class FunctionKeywordArgument(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume("")
	}
}*/


class For(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("for ") == null)
			return false

		consume(Whitespace::class)

		if (consume(Variable::class) == null)
			return false

		consume(Whitespace::class)

		if (consume("in ") == null)
			return false

		consume(Whitespace::class)

		if (consume(Expression::class) == null)
			return false

		consume(Whitespace::class)

		if (consume(CodeBlock::class) == null)
			syntaxError("Expected indented code block below")

		return true
	}
}


class While(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("while ") == null)
			return false

		if (consume(Expression::class) == null)
			syntaxError("Expected expression")

		if (consume(CodeBlock::class) == null)
			syntaxError("Expected indented code block below")

		return true
	}

}