package net.merayen.elastic.backend.script.highlang

class Validation(token: CodeBlock) {
	enum class Severity {
		INFO, WARNING, ERROR
	}

	enum class Type(val severity: Severity) {
		VARIABLE_NOT_DECLARED(Severity.ERROR),
		VARIABLE_ALREADY_DECLARED(Severity.ERROR)
	}

	class Item(val token: Token, val type: Type)

	val items = ArrayList<Item>()
	private val insight = Insight(token)
	private val scopeTokens = insight.getScopeTokens()

	val infos
		get() = items.filter { it.type.severity == Severity.INFO }

	val warnings
		get() = items.filter { it.type.severity == Severity.WARNING }

	val errors
		get() = items.filter { it.type.severity == Severity.ERROR }

	init {
		checkVariables()
	}

	fun checkVariables() {
		//val declared = insight.getDeclaredVariables()
		//val used = insight.getVariables()
		val declared = HashMap<String, Token>()
		val used = HashMap<String, Token>()

		for (token in scopeTokens) {
			if (token is VariableWrite) {
				//println("VariableWrite " + token.variable.name)
				if (token.variable.name !in declared)
					declared[token.variable.name] = token
				else
					report(token, Type.VARIABLE_ALREADY_DECLARED)
			} else if (token is VariableRead) {
				//println("VariableRead " + token.variable.name)
				if (token.variable.name in declared)
					used[token.variable.name] = token
				else
					report(token, Type.VARIABLE_NOT_DECLARED)
			}
		}
	}

	private fun report(token: Token, type: Type) {
		items.add(Item(token, type))
	}
}