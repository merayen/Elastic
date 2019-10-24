object Tokens {
	abstract class JSToken {
		protected abstract fun onProcess(): String

		fun process(token: JSToken): String = token.onProcess()
	}

	abstract class JSExpression : JSToken()

	class JSTop(val token: JSToken) : JSToken() {
		override fun onProcess() = process(token)
	}

	class JSCodeBlock(val statements: Array<JSStatement>) : JSToken() {
		override fun onProcess() = statements.joinToString("") { process(it) }
	}

	class JSFunction(val arguments: Array<JSVariable>, val body: JSCodeBlock) : JSExpression() {
		override fun onProcess() = "function(${arguments.joinToString(",") { process(it) }}){${process(body)}}"
	}

	class JSFunctionCall(val arguments: JSExpressionList) : JSExpression() {
		override fun onProcess() = "(${process(arguments)})"
	}

	class JSParanthesis(val expressionList: JSExpressionList) : JSExpression() {
		init {
			if (expressionList.expressions.isEmpty())
				throw RuntimeException("Parenthesis must contain at least 1 expression")
		}

		override fun onProcess() = "(${process(expressionList)})"
	}

	class JSIndexReference(val expressionList: JSExpressionList) : JSExpression() {
		override fun onProcess() = "[$expressionList]"
	}

	abstract class JSStatement : JSToken()

	class JSExpressionStatement(val expressionList: JSExpressionList) : JSStatement() {
		override fun onProcess() = "${process(expressionList)};"
	}

	class JSVarDeclaration(val name: String, val expressionList: JSExpressionList) : JSStatement() {
		override fun onProcess() = "var $name=${process(expressionList)};"
	}

	class JSReturnStatement(val expressionList: JSExpressionList) : JSStatement() {
		override fun onProcess() = "return ${process(expressionList)};"
	}

	class JSIfStatement(val ifBlocks: Array<IfBlock>, val elseBlock: JSCodeBlock? = null) : JSStatement() {
		class IfBlock(val condition: JSExpressionList, val codeBlock: JSCodeBlock)

		init {
			if (ifBlocks.isEmpty())
				throw RuntimeException("Must be at least 1 condition for if-statement")
		}

		override fun onProcess(): String {
			val result = StringBuilder()

			result.append("if(${process(ifBlocks[0].condition)}){${process(ifBlocks[0].codeBlock)}}")

			for (ifBlock in ifBlocks.sliceArray(IntRange(1, ifBlocks.size - 1))) {
				result.append("else if(${process(ifBlock.condition)}){${process(ifBlock.codeBlock)}}")
			}

			if (elseBlock != null)
				result.append("else{${process(elseBlock)}}")

			return result.toString()
		}
	}

	class JSExpressionList(val expressions: Array<JSExpression>) : JSToken() {
		override fun onProcess() = expressions.joinToString("") { process(it) }

	}

	class JSArray(val expressionList: JSExpressionList) : JSExpression() {
		override fun onProcess() = "[${process(expressionList)}]"

	}

	class JSVariable(val name: String) : JSExpression() {
		override fun onProcess() = name

	}

	class JSString(val text: String) : JSExpression() {
		override fun onProcess() = "\"${text.replace("\"", "\\\"")}\""
	}

	class JSOperator(val type: Type) : JSExpression() {

		enum class Type(val code: String) {
			PLUS("+"),
			MINUS("-"),
			MULTIPLY("*"),
			DIVIDE("/"),
			MODULO("%"),
			EQUAL("=="),
			NOT_EQUAL("!="),
			REF_EQUAL("==="),
			REF_NOT_EQUAL("!=="),
			MORE_THAN(">"),
			MORE_THAN_OR_EQUAL(">="),
			LESS_THAN("<"),
			LESS_THAN_OR_EQUAL("<="),
			DOT("."),
			BITWISE_XOR("^"),
			BITWISE_AND("&"),
			BITWISE_OR("|"),
			BITWISE_NOT("!"),
			AND("&&"),
			OR("||"),
			COMMA(",")
		}

		override fun onProcess() = type.code
	}
}

fun main() {
	val top = Tokens.JSTop(
		Tokens.JSCodeBlock(
			arrayOf(
				Tokens.JSExpressionStatement(
					Tokens.JSExpressionList(
						arrayOf(
							Tokens.JSParanthesis(
								Tokens.JSExpressionList(
									arrayOf(
										Tokens.JSFunction(
											arguments = arrayOf(
												Tokens.JSVariable("name"),
												Tokens.JSVariable("age")
											),
											body = Tokens.JSCodeBlock(
												arrayOf(
													Tokens.JSVarDeclaration(
														"my_text",
														Tokens.JSExpressionList(
															arrayOf(
																Tokens.JSArray(
																	Tokens.JSExpressionList(
																		arrayOf(
																			Tokens.JSString("Hello"),
																			Tokens.JSOperator(Tokens.JSOperator.Type.COMMA),
																			Tokens.JSString("world!")
																		)
																	)
																),
																Tokens.JSOperator(Tokens.JSOperator.Type.DOT),
																Tokens.JSVariable("map"),
																Tokens.JSFunctionCall(
																	arguments = Tokens.JSExpressionList(
																		arrayOf(
																			Tokens.JSFunction(
																				arguments = arrayOf(
																					Tokens.JSVariable("x")
																				),
																				body = Tokens.JSCodeBlock(
																					arrayOf(
																						Tokens.JSReturnStatement(
																							Tokens.JSExpressionList(
																								arrayOf(
																									Tokens.JSString(" (>^_^)>"),
																									Tokens.JSOperator(Tokens.JSOperator.Type.PLUS),
																									Tokens.JSVariable("x"),
																									Tokens.JSOperator(Tokens.JSOperator.Type.PLUS),
																									Tokens.JSString("<(^_^<) ")
																								)
																							)
																						)
																					)
																				)
																			)
																		)
																	)
																)
															)
														)
													),
													Tokens.JSExpressionStatement(
														Tokens.JSExpressionList(
															arrayOf(
																Tokens.JSVariable("alert"),
																Tokens.JSFunctionCall(
																	Tokens.JSExpressionList(
																		arrayOf(
																			Tokens.JSVariable("my_text"),
																			Tokens.JSOperator(Tokens.JSOperator.Type.DOT),
																			Tokens.JSVariable("join"),
																			Tokens.JSFunctionCall(
																				arguments = Tokens.JSExpressionList(
																					arrayOf(
																						Tokens.JSString("")
																					)
																				)
																			)
																		)
																	)
																)
															)
														)
													)
												)
											)
										)
									)
								)
							),
							Tokens.JSFunctionCall(
								arguments = Tokens.JSExpressionList(
									arrayOf()
								)
							)
						)
					)
				)
			)
		)
	)

	println(top.process(top))
}