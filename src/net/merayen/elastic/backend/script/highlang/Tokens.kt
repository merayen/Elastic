package net.merayen.elastic.backend.script.highlang

import java.lang.RuntimeException
import kotlin.math.max
import kotlin.reflect.KClass

abstract class Token(private val lexer: Lexer) {
	inner class SyntaxError(/*token: Token, */ description: String) : java.lang.RuntimeException(
			"Line ${lexer.currentLineNumber()}, position ${lexer.currentLinePosition()}: $description\n" +
					lexer.currentLine() + "\n" + (" ".repeat(max(0, lexer.currentLinePosition() - 1)) + '^'))

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
	protected fun consume(textArray: Array<String>) = lexer.consume(textArray)
	protected fun consume(token: KClass<out Token>) = lexer.consume(token)
	protected fun consume(regex: Regex) = lexer.consume(regex)

	protected fun startsWith(string: String) = lexer.source.startsWith(string)
	protected val currentLine: String
		get() = lexer.currentLine()

	protected val position: Int
		get() = lexer.cursor

	protected fun debug() = println("DEBUG: " + lexer.currentLine() + "\n" + " ".repeat(lexer.currentLinePosition() + 6) + "^")

	override fun toString() = this::class.simpleName + "(" + onToString().joinToString(", ") + ")"
}


class VariableDeclaration(lexer: Lexer) : Token(lexer) {
	val variableName: String
		get() {
			for(token in children)
				if (token is Variable)
					return token.name

			throw RuntimeException()
		}

	val variableType: String
		get() {
			for(token in children)
				if (token is TypeDeclaration)
					return token.type

			return "fp32"  // Default when not defined
		}

	override fun onToString() = arrayOf<Any?>()

	override fun onExecute(): Boolean {
		consume(Whitespace::class)
		if (consume("var ") == null)
			return false

		consume(Variable::class) == null ?: throw SyntaxError("Expected a variable name after 'var'")

		consume(Whitespace::class)

		if (consume(":") != null) {
			consume(Whitespace::class)
			consume(TypeDeclaration::class) == null ?: throw SyntaxError("Expected type hinting")

			consume(Whitespace::class)
		}

		if (consume("=") != null) {
			consume(Whitespace::class)
			consume(Number::class) == null ?: throw SyntaxError("Expected a number")
		}

		return true
	}
}


class TypeDeclaration(lexer: Lexer) : Token(lexer) {
	private val types = arrayOf("fp16", "fp32", "fp64")

	lateinit var type: String
		private set

	override fun onExecute(): Boolean {
		type = consume(types) ?: throw java.lang.RuntimeException()

		return true
	}

	override fun onToString() = arrayOf<Any?>(type)
}


class Variable(lexer: Lexer) : Token(lexer) {
	lateinit var name: String
		private set

	override fun onExecute(): Boolean {
		name = consume(Regex("^[a-zA-Z_]+")) ?: return false

		if (consume("[") != null) {
			consume(Whitespace::class)

			consume(Expression::class) == null ?: throw SyntaxError("Expected expression in array index")

			consume(Whitespace::class)
			consume("]")
		}

		return true
	}

	override fun onToString() = arrayOf<Any?>(name)
}


class VariableSet(lexer: Lexer): Token(lexer) {
	lateinit var operation: String
		private set

	val operators = arrayOf("=", "+=", "-=", "*=", "/=", "%=", "|=", "&=", "^=")

	override fun onExecute(): Boolean {
		val variableName = consume(Variable::class)
		if (variableName == null)
			return false

		consume(Whitespace::class)

		operation = consume(operators) ?: return false

		consume(Whitespace::class)

		consume(Expression::class) == null ?: throw SyntaxError("Expected an expression")

		return true
	}
}


/**
 * Eats all whitespaces
 */
class Whitespace(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		var lastPosition = position
		while (true) {
			consume(" ")
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
 * Consumes all whitespaces and line breaks
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

			val emptySpace = consume(EmptySpace::class)

			if (consume("}") != null)
				break // End of CodeBlock

			emptySpace ?: throw SyntaxError("Expected whitespace or line break between statements")

			if (lastPosition == position)
				throw SyntaxError("EOF. Expected '}'")

			lastPosition = position
		}

		return true
	}
}


class Statement(lexer: Lexer) : Token(lexer) {
	private val possible = arrayOf(
			VariableDeclaration::class,
			For::class,
			While::class,
			If::class,
			Return::class,
			FunctionCall::class,
			FunctionDeclaration::class,
			VariableSet::class,
			Pass::class
	)

	override fun onExecute(): Boolean {
		consume(Whitespace::class)
		for (tokenClass in possible)
			if (consume(tokenClass) != null)
				return true

		throw SyntaxError("Invalid statement")
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
		if (variable == null)
			return false

		val args = consume(FunctionCallArguments::class)
		if (args == null)
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
			consume(Whitespace::class)
			consume(Expression::class)
			consume(Whitespace::class)
			consume(",")

			if (lastPosition == position)
				throw SyntaxError("Invalid function arguments")

			lastPosition = position
		}

		return true
	}
}


class Expression(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		while (true) {
			when {
				consume(Number::class) != null -> {}
				consume(FunctionCall::class) != null -> {}
				consume(Variable::class) != null -> {}
				consume(ExpressionParentheses::class) != null -> {}
				else -> throw SyntaxError("Expected a valid expression")
			}

			consume(Whitespace::class)

			if (consume(Operator::class) != null)
				;
			else if (consume(ExpressionIfElse::class) != null)
				break
			else
				break

			consume(Whitespace::class)
		}
		return true
	}
}


class ExpressionParentheses(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("(") == null)
			return false

		consume(Whitespace::class)
		consume(Expression::class)
		consume(Whitespace::class)

		if (consume(")") == null)
			throw SyntaxError("Expected ')'")

		return true
	}
}


class ExpressionIfElse(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("if ") == null)
			return false

		consume(Whitespace::class)

		consume(Expression::class) ?: throw SyntaxError("Expected expression after 'if'")

		consume(Whitespace::class)

		consume("else ") ?: throw SyntaxError("Expected 'else' after expression")

		consume(Whitespace::class)

		consume(Expression::class) ?: throw SyntaxError("Expected expression after 'else'")

		return true
	}
}

class Operator(lexer: Lexer) : Token(lexer) {
	lateinit var operator: String
		private set

	override fun onExecute(): Boolean {
		operator = consume(operators) ?: return false

		return true
	}

	override fun onToString() = arrayOf<Any?>(operator)

	companion object {
		private val operators = arrayOf(
				"+",
				"-",
				"**",
				"*",
				"/",
				"%",
				"&",
				"^",
				"|",
				"and",
				"or",
				">",
				">=",
				"==",
				"<=",
				"!="
		)
	}
}


class FunctionDeclaration(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("def ") == null)
			return false

		val name = consume(Variable::class) ?: return false

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


class If(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("if ") == null)
			return false

		consume(Whitespace::class)

		if (consume(Expression::class) == null)
			syntaxError("Expected expression")

		consume(Whitespace::class)

		if (consume(CodeBlock::class) == null)
			syntaxError("Expected indented code block below")

		consume(Whitespace::class)

		while (consume(IfElif::class) != null);

		consume(Whitespace::class)

		consume(IfElse::class)

		return true
	}
}


class IfElif(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("elif ") == null)
			return false

		if (consume(Expression::class) == null)
			syntaxError("Expected expression")

		consume(Whitespace::class)

		if (consume(CodeBlock::class) == null)
			syntaxError("Expected indented code block below")

		return true
	}
}


class IfElse(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("else ") == null)
			return false

		consume(Whitespace::class)

		if (consume(CodeBlock::class) == null)
			syntaxError("Expected indented code block below")

		return true
	}
}


class Return(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		if (consume("return ") == null)
			return false

		consume(Whitespace::class)

		consume(Expression::class)

		return true
	}
}