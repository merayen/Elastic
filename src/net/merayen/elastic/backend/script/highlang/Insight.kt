package net.merayen.elastic.backend.script.highlang

class Insight(token: CodeBlock) {
	class Context(private val token: CodeBlock) {
		class ScopeVariable(val name: String, val type: VariableTypes, val token: Variable)
		class FunctionScope(val name: String, val arguments: ArrayList<VariableWrite>, val context: Context)

		val scopeTokens = calculateScopeTokens()

		//val variables = calculateVariables()
		val writeVariables = ArrayList<VariableWrite>()
		//val readVariables = ArrayList<VariableWrite>()

		val functions = ArrayList<FunctionScope>()

		/**
		 * Retrieves all tokens for the current scope.
		 */
		private fun calculateScopeTokens(): ArrayList<Token> {
			val result = ArrayList<Token>()

			LexerTraverse(token).walk { path, token ->
				result.add(token)

				when {
					path.size == 0 -> true
					token is FunctionDeclaration -> {
						result.add(token.variable)
						false // Don't walk into functions as they have their own scope
					}
					token is NativeFunctionDeclaration -> {
						result.add(token.variable)
						false // Don't walk into functions as they have their own scope
					}
					else -> true
				}
			}

			return result
		}

		/**
		 * Get all variable usages in current scope
		 */
		private fun calculateVariables(): ArrayList<VariableWrite> {
			val result = ArrayList<VariableWrite>()

			for (token in scopeTokens)
				if (token is VariableWrite)
					result.add(token)

			return result
		}
	}

	val context = Context(token)

	/**
	 * Get all variables declared in the scope.
	 */
	/*fun getDeclaredVariables(): ArrayList<VariableDeclaration> {
		val result = ArrayList<VariableDeclaration>()

		for (token in tokens)
			if (token is VariableDeclaration)
				result.add(token)

		return result
	}*/
}