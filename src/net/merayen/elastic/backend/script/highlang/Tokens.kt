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

	/**
	 * Which line the parser is currently at.
	 * N/A when not parsing.
	 */
	protected val parserCurrentLine: String
		get() = lexer.currentLine()

	/**
	 * Position the parser is currently at.
	 * N/A when not parsing.
	 */
	protected val parserPosition: Int
		get() = lexer.cursor

	/**
	 * Where the parser found the beginning of the token
	 */
	val position = lexer.cursor

	/**
	 * Where on the line the token starts
	 */
	val linePosition = lexer.currentLinePosition()

	/**
	 * Which line the parser found the token
	 */
	val line = lexer.currentLineNumber()

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

	val variableType: VariableTypes
		get() {
			for(token in children)
				if (token is TypeDeclaration)
					return token.type

			return VariableTypes.FP32  // Default when not defined
		}

	override fun onToString() = arrayOf<Any?>()

	override fun onExecute(): Boolean {
		consume(Whitespace::class)

		consume("var ") ?: return false

		consume(VariableWrite::class) ?: throw SyntaxError("Expected a variable name after 'var'")

		consume(Whitespace::class)

		if (consume("=") != null) {
			consume(Whitespace::class)
			consume(Number::class) ?: throw SyntaxError("Expected a number")
		}

		return true
	}
}


class TypeDeclaration(lexer: Lexer) : Token(lexer) {
	private val types = arrayOf(
			VariableTypes.INT8,
			VariableTypes.INT16,
			VariableTypes.INT32,
			VariableTypes.INT64,
			VariableTypes.FP16,
			VariableTypes.FP32,
			VariableTypes.FP64
	)

	lateinit var type: VariableTypes
		private set

	override fun onExecute(): Boolean {
		val typeString = consume(types.map { it.value }.toTypedArray()) ?: throw SyntaxError("Unknown type")

		type = VariableTypes.getByValue(typeString) ?: throw SyntaxError("Unknown type")

		return true
	}

	override fun onToString() = arrayOf<Any?>(type)
}


class VariableRead(lexer: Lexer) : Token(lexer) {
	lateinit var variable: Variable
	override fun onExecute(): Boolean {
		variable = (consume(Variable::class) ?: return false) as Variable
		return true
	}
}


class VariableWrite(lexer: Lexer) : Token(lexer) {
	lateinit var variable: Variable
	lateinit var type: VariableTypes

	override fun onExecute(): Boolean {
		variable = (consume(Variable::class) ?: return false) as Variable

		consume(Whitespace::class)

		if (consume(":") != null) {
			consume(Whitespace::class)
			val typeDeclaration = (consume(TypeDeclaration::class) ?: throw SyntaxError("Expected type hinting")) as TypeDeclaration
			type = typeDeclaration.type
			consume(Whitespace::class)
		}

		return true
	}
}


class Variable(lexer: Lexer) : Token(lexer) {
	lateinit var name: String
		private set

	override fun onExecute(): Boolean {
		name = consume(Regex("^[a-zA-Z_]+")) ?: return false

		if (consume("[") != null) {
			consume(Whitespace::class)

			consume(Expression::class) ?: throw SyntaxError("Expected expression in array index")

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
		val variableName = consume(VariableRead::class)

		variableName ?: return false

		consume(Whitespace::class)

		operation = consume(operators) ?: return false

		consume(Whitespace::class)

		consume(Expression::class) ?: throw SyntaxError("Expected an expression")

		return true
	}
}


/**
 * Eats all whitespaces
 */
class Whitespace(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		var lastPosition = parserPosition
		while (true) {
			consume(" ")
			consume("\t")
			consume("\r")

			if (lastPosition == parserPosition)
				break

			lastPosition = parserPosition
		}

		return true
	}
}


/**
 * Consumes all whitespaces and line breaks
 */
class EmptySpace(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		val initial = parserPosition
		var lastPosition = parserPosition
		while (true) {
			consume(" ")
			consume("\t")
			consume("\n")

			if (lastPosition == parserPosition)
				break

			lastPosition = parserPosition
		}

		return initial != parserPosition
	}
}


/**
 * A tab indent, meaning a code block
 */
class CodeBlock(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume(Whitespace::class)

		consume("{") ?: return false // Not a CodeBlock

		var lastPosition = parserPosition
		while (true) {
			consume(EmptySpace::class)

			consume(Statement::class)

			val emptySpace = consume(EmptySpace::class)

			if (consume("}") != null)
				break // End of CodeBlock

			emptySpace ?: throw SyntaxError("Expected whitespace or line break between statements")

			if (lastPosition == parserPosition)
				throw SyntaxError("EOF. Expected '}'")

			lastPosition = parserPosition
		}

		return true
	}
}


class Statement(lexer: Lexer) : Token(lexer) {
	private val possible = arrayOf(
			VariableDeclaration::class,
			Import::class,
			For::class,
			While::class,
			If::class,
			Return::class,
			FunctionCall::class,
			NativeFunctionDeclaration::class,
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


class Import(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume("import ") ?: return false
		consume(VariableWrite::class)
		return true
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


class FunctionCall(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume(VariableRead::class) ?: return false
		consume(FunctionCallArguments::class) ?: return false
		return true
	}
}


class FunctionCallArguments(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume("(") ?: return false

		var lastPosition = parserPosition
		while (consume(")") == null) {
			consume(Whitespace::class)
			consume(Expression::class)
			consume(Whitespace::class)
			consume(",")

			if (lastPosition == parserPosition)
				throw SyntaxError("Invalid function arguments")

			lastPosition = parserPosition
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
				consume(VariableRead::class) != null -> {}
				consume(ExpressionParentheses::class) != null -> {}
				else -> throw SyntaxError("Expected a valid expression")
			}

			consume(Whitespace::class)

			if (consume(Operator::class) != null)

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
		consume("(") ?: return false

		consume(Whitespace::class)
		consume(Expression::class)
		consume(Whitespace::class)

		consume(")") ?: throw SyntaxError("Expected ')'")

		return true
	}
}


class ExpressionIfElse(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume("if ") ?: return false

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
				".",
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
				"<",
				">",
				">=",
				"==",
				"<=",
				"!="
		)
	}
}


class FunctionDeclaration(lexer: Lexer) : Token(lexer) {
	lateinit var variable: VariableWrite
		private set

	override fun onExecute(): Boolean {
		consume("def ") ?: return false

		variable = (consume(VariableWrite::class) ?: throw SyntaxError("Expected name of function")) as VariableWrite

		consume(Whitespace::class)
		consume(FunctionArguments::class) ?: throw SyntaxError("Expected tuple with function arguments")
		consume(CodeBlock::class) ?: throw SyntaxError("Expected indented code block below function definition")

		return true
	}
}


class NativeFunctionDeclaration(lexer: Lexer) : Token(lexer) {
	lateinit var variable: VariableWrite
		private set

	override fun onExecute(): Boolean {
		consume("native def ") ?: return false

		variable = (consume(VariableWrite::class) ?: throw SyntaxError("Expected name of function")) as VariableWrite

		consume(Whitespace::class)
		consume(FunctionArguments::class) ?: throw SyntaxError("Expected tuple with function arguments")

		return true
	}
}


class FunctionArguments(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume("(") ?: return false

		var lastPosition = parserPosition
		while (consume(")") == null) {
			consume(Whitespace::class)
			consume(VariableWrite::class)
			consume(Whitespace::class)
			consume(",")

			if (lastPosition == parserPosition)
				throw SyntaxError("Invalid function arguments")

			lastPosition = parserPosition
		}

		return true
	}
}


class For(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume("for ") ?: return false

		consume(Whitespace::class)
		consume(VariableWrite::class) ?: return false
		consume(Whitespace::class)

		consume("in ") ?: return false

		consume(Whitespace::class)
		consume(Expression::class) ?: return false
		consume(Whitespace::class)

		consume(CodeBlock::class) ?: throw SyntaxError("Expected indented code block below")

		return true
	}
}


class While(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume("while ") ?: return false

		consume(Expression::class) ?: throw SyntaxError("Expected expression")
		consume(Whitespace::class)
		consume(CodeBlock::class) ?: throw SyntaxError("Expected indented code block below")

		return true
	}
}


class If(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume("if ") ?: return false

		consume(Whitespace::class)
		consume(Expression::class) ?: throw SyntaxError("Expected expression")
		consume(Whitespace::class)
		consume(CodeBlock::class) ?: throw SyntaxError("Expected indented code block below")
		consume(Whitespace::class)
		while (consume(IfElif::class) != null);
		consume(Whitespace::class)
		consume(IfElse::class)

		return true
	}
}


class IfElif(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume("elif ") ?: return false

		consume(Expression::class) ?: throw SyntaxError("Expected expression")

		consume(Whitespace::class)

		consume(CodeBlock::class) ?: throw SyntaxError("Expected indented code block below")

		return true
	}
}


class IfElse(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume("else ") ?: return false

		consume(Whitespace::class)

		consume(CodeBlock::class) ?: throw SyntaxError("Expected indented code block below")

		return true
	}
}


class Return(lexer: Lexer) : Token(lexer) {
	override fun onExecute(): Boolean {
		consume("return ") ?: return false

		consume(Whitespace::class)

		consume(Expression::class)

		return true
	}
}