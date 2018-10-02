package net.merayen.elastic.backend.script.highlang

class Insight(private val token: Token) {
	private val lexerTraverse = LexerTraverse(token)

	/**
	 * Retrieve all available functions
	 */
	fun getFunctions() {
		/*lexerTraverse.walk(null, (x) ->
			FunctionDeclaration.isInstance(it)
		)*/
	}

	/**
	 * Get all variables declared in the scope.
	 */
	fun getDeclaredVariables(): ArrayList<VariableDeclaration> {
		val result = ArrayList<VariableDeclaration>()

		for(token in getScopeTokens())
			if (token is VariableDeclaration)
				result.add(token)

		return result
	}

	/**
	 * Get all variable usages in current scope
	 */
	fun getVariables(): ArrayList<Variable> {
		val result = ArrayList<Variable>()

		for (token in getScopeTokens()) {
			if (token is Variable)
				result.add(token)
		}

		return result
	}

	/**
	 * Retrieves all tokens for the current scope.
	 */
	fun getScopeTokens(): ArrayList<Token> {
		val result = ArrayList<Token>()

		lexerTraverse.walk {
			path, token ->
			result.add(token)
			token !is FunctionDeclaration
		}

		return result
	}
}