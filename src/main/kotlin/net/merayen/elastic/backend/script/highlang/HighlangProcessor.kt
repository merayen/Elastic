package net.merayen.elastic.backend.script.highlang

/**
 * Processes a Token-tree and creates Scopes of it.
 * Also validates underway.
 */
class HighlangProcessor(token: CodeBlock) {
	class Scope(val parent: Scope? = null, val token: CodeBlock, val inheritedVariables: HashMap<String, VariableWrite> = HashMap()) {

		/**
		 * All tokens in the current scope, in a flat list for easy iteration.
		 */
		val scopeTokens = calculateScopeTokens()

		/**
		 * All variables, including inherited ones, in this scope.
		 */
		val variables = calculateVariables()

		/**
		 * Children scopes, like inner code blocks for functions, for-loops, while-loops and ifs.
		 */
		val children = calculateChildren()

		private fun calculateVariables(): HashMap<String, VariableWrite> {
			val result = HashMap<String, VariableWrite>()

			// Add inherited variables
			result.putAll(inheritedVariables)

			// Overwrite inherited variables with our local ones
			for (token in scopeTokens)
				if (token is VariableWrite)
					result[token.variable.name] = token

			return result
		}

		/**
		 * Retrieves all tokens for the current scope.
		 * For caching and quicker access.
		 * List is flat.
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
					token is For -> false
					token is While -> false
					token is If -> false
					else -> true
				}
			}

			return result
		}

		private fun calculateChildren(): HashMap<CodeBlock, Scope> {
			val result = HashMap<CodeBlock, Scope>()

			LexerTraverse(token).walk { path, token ->
				when {
					path.size == 0 -> true
					token is FunctionDeclaration -> {
						// Add the function arguments into the scope
						val funcVariables = HashMap<String, VariableWrite>(variables)
						funcVariables.putAll(token.functionArguments)

						result[token.codeBlock] = Scope(this, token.codeBlock, funcVariables)
						false
					}
					token is For -> {
						// Add the function arguments into the scope
						val funcVariables = HashMap<String, VariableWrite>(variables)
						funcVariables.put(token.loopVariable.variable.name, token.loopVariable)

						result[token.codeBlock] = Scope(this, token.codeBlock, funcVariables)
						false
					}
					token is While -> {
						result[token.codeBlock] = Scope(this, token.codeBlock, variables)
						false
					}
					token is If -> {
						for (x in token.codeBlocks)
							result[x] = Scope(this, x, variables)
						false
					}
					else -> true
				}
			}

			return result
		}
	}

	val scope = Scope(null, token)
}
