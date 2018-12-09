package net.merayen.elastic.backend.script.highlang.backends.interpreter

import net.merayen.elastic.backend.script.highlang.*
import net.merayen.elastic.backend.script.highlang.backends.AbstractRuntime
import net.merayen.elastic.util.UniqueID
import java.util.*
import kotlin.collections.HashMap

class InterpreterRuntime(private val highlangProcessor: HighlangProcessor) : AbstractRuntime() {
	class StackEntry(val scope: HighlangProcessor.Scope, variables: HashMap<String, VariableEntry>, val name: String = UniqueID.create())
	class VariableEntry(val name: String, val type: VariableTypes, var value: Double = 0.0)

	val stack = ArrayDeque<StackEntry>()

	var topScope: HighlangProcessor.Scope? = null

	override fun run() {
		importModules()
		runScope(highlangProcessor.scope, HashMap())
	}

	/**
	 * Imports all modules that are mentioned in the current source
	 */
	private fun importModules() {
		//highlangProcessor.scope.token =
	}

	private fun runScope(scope: HighlangProcessor.Scope, inheritedVariables: HashMap<String, VariableEntry>) {
		// What should happen here? We should inherit variables from the previous scope
		// ...but only if we are a direct child of that scope. Lol. no. Wrong logic...

		// Inherit variables from parent scope
		val scopeVariables = HashMap<String, VariableEntry>(inheritedVariables)

		// Add (or overwrite) local scope variables
		/*for (v in scope.variables)
			scopeVariables[v.key] = VariableEntry(v.key, v.value.type)

		stack.add(StackEntry(scope, inheritedVariables))

		for (token in scope.token.children) {
			when (token) {
				is CodeBlock -> runScope(scope.children[token]!!)
				is Statement -> {
					val statementToken = token.children[0]
					when (statementToken) {
						is VariableDeclaration -> {
							val variable = scopeVariables[statementToken.variableName] ?: throw RuntimeException("Declared a variable not detected in scope")
							variable.value = statementToken.variableValue
						}
					}
				}
				else -> throw RuntimeException("Could not understand token $token")
			}
		}
		stack.pollLast()*/
	}

	override fun getRuntimeVariables(): java.util.HashMap<String, Any> {
		TODO("not implemented")
	}

	/**
	 * Initializes the variables in the top-most scope.
	 */
	private fun initStackVariables() {
		val variables = HashMap<String, VariableEntry>()

		var currentScope = stack.last.scope

		// Trace backwards
		for (s in stack.reversed().subList(1, stack.size - 1)) {

		}
	}
}
