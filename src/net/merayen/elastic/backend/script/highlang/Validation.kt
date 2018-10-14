package net.merayen.elastic.backend.script.highlang

class Validation(private val highlangProcessor: HighlangProcessor) {
	enum class Severity {
		INFO, WARNING, ERROR
	}

	enum class Type(val severity: Severity) {
		VARIABLE_NOT_DECLARED(Severity.ERROR),
		VARIABLE_ALREADY_DECLARED_IN_SCOPE(Severity.ERROR)
	}

	class Item(val token: Token, val type: Type)

	val items = ArrayList<Item>()

	val infos
		get() = items.filter { it.type.severity == Severity.INFO }

	val warnings
		get() = items.filter { it.type.severity == Severity.WARNING }

	val errors
		get() = items.filter { it.type.severity == Severity.ERROR }

	init {
		checkVariables()
	}

	private fun checkVariables() {
		fun processScope(scope: HighlangProcessor.Scope) {
			val declared = HashMap<String, Token>()
			for (token in scope.scopeTokens) {
				if (token is VariableWrite) {
					if (token.variable.name !in declared)
						declared[token.variable.name] = token
					else
						report(token, Type.VARIABLE_ALREADY_DECLARED_IN_SCOPE)
				} else if (token is VariableRead) {
					if (token.variable.name !in declared && token.variable.name !in scope.inheritedVariables)
						report(token, Type.VARIABLE_NOT_DECLARED)
				}
			}

			for (childScope in scope.children.values)
				processScope(childScope)
		}

		processScope(highlangProcessor.scope)
	}



	private fun report(token: Token, type: Type) {
		items.add(Item(token, type))
	}
}